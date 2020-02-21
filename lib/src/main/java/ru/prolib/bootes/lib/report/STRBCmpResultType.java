package ru.prolib.bootes.lib.report;

public enum STRBCmpResultType {
	/**
	 * Both reports blocks are completely identical.
	 */
	IDENTICAL,
	/**
	 * Cannot identity report block because header is of unknown format.
	 */
	HEADER_MALFORMED,
	/**
	 * Report block header points to another report ID or has another title.
	 */
	HEADER_MISMATCH,
	/**
	 * Report block content i not identical with expected.
	 */
	REPORT_MISMATCH,
	/**
	 * An error occurred during report block comparison.
	 */
	REPORT_COMPARE_FAILED,
	/**
	 * Report block not exists.
	 */
	NOT_EXISTS,
	/**
	 * Report block is unexpected.
	 */
	UNEXPECTED,
}
