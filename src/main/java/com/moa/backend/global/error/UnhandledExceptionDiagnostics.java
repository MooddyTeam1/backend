package com.moa.backend.global.error;

import java.sql.SQLException;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.CannotCreateTransactionException;

/**
 * {@link GlobalExceptionHandler} 의 미처리 예외에 대해, DB 커넥션 고갈·스레드 풀 거부 등
 * 원인을 로그/진단 문자열로 구분하기 위한 분류기.
 *
 * <p>참고: Tomcat 워커 스레드가 모두 점유된 경우 요청이 컨트롤러까지 오지 않고
 * 큐 적체·타임아웃·503으로 끝날 수 있어, 이 분류기로는 잡히지 않을 수 있다.
 */
public final class UnhandledExceptionDiagnostics {

    public enum Category {
        /** Hikari 등에서 커넥션을 못 얻음 */
        JDBC_CONNECTION_POOL,
        /** Executor/스레드 풀에서 작업 거부 */
        THREAD_POOL_REJECTED,
        /** 트랜잭션 시작 시 리소스(DB) 문제로 래핑된 경우 */
        TRANSACTION_RESOURCE,
        /** 위에 해당하지 않거나 원인 불명 */
        OTHER
    }

    public record Result(Category category, String rootCauseClassName, String shortSummary) {}

    private UnhandledExceptionDiagnostics() {}

    public static Result analyze(Throwable ex) {
        if (ex == null) {
            return new Result(Category.OTHER, "null", "");
        }
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String rootName = root.getClass().getName();

        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof CannotGetJdbcConnectionException) {
                return jdbcPool(t, rootName);
            }
            if (t instanceof CannotCreateTransactionException) {
                Throwable cause = t.getCause();
                if (cause instanceof CannotGetJdbcConnectionException || isSqlConnectionTimeout(cause)) {
                    return jdbcPool(t, rootName);
                }
                return new Result(
                        Category.TRANSACTION_RESOURCE,
                        rootName,
                        "CannotCreateTransactionException: " + safeMsg(cause));
            }
            if (t instanceof DataAccessResourceFailureException) {
                return new Result(
                        Category.TRANSACTION_RESOURCE,
                        rootName,
                        "DataAccessResourceFailureException: " + safeMsg(t));
            }
            if (t instanceof RejectedExecutionException) {
                return new Result(
                        Category.THREAD_POOL_REJECTED,
                        rootName,
                        "RejectedExecutionException: " + safeMsg(t));
            }
            if (isSqlConnectionTimeout(t)) {
                return jdbcPool(t, rootName);
            }
        }

        String summary = ex.getMessage() != null ? ex.getClass().getSimpleName() + ": " + ex.getMessage() : ex.getClass().getSimpleName();
        return new Result(Category.OTHER, rootName, truncate(summary, 300));
    }

    private static Result jdbcPool(Throwable marker, String rootName) {
        return new Result(
                Category.JDBC_CONNECTION_POOL,
                rootName,
                marker.getClass().getSimpleName() + ": " + safeMsg(marker));
    }

    private static boolean isSqlConnectionTimeout(Throwable t) {
        if (t == null) {
            return false;
        }
        if (t instanceof java.sql.SQLTransientConnectionException) {
            return true;
        }
        if (t instanceof SQLException) {
            String state = ((SQLException) t).getSQLState();
            String msg = safeMsg(t).toLowerCase();
            return msg.contains("timeout") || msg.contains("connection is not available") || msg.contains("pool");
        }
        String name = t.getClass().getName();
        return name.contains("hikari") && (safeMsg(t).toLowerCase().contains("timeout") || safeMsg(t).toLowerCase().contains("connection"));
    }

    private static String safeMsg(Throwable t) {
        if (t == null) {
            return "";
        }
        String m = t.getMessage();
        return m != null ? m : "";
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
