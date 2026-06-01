package library_manage.controller;

import java.util.ArrayList;
import library_manage.Model.BorrowOperation;
import library_manage.util.ServiceResult;

public class ReportController {

    private final TransactionController transactionController;

    public ReportController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    public ServiceResult getBorrowHistory() {
        return transactionController.getBorrowHistory();
    }

    public ServiceResult getCurrentBorrowedBooks() {
        return transactionController.getCurrentBorrowedBooks();
    }

    public ServiceResult getMostBorrowedBooks() {
        return transactionController.getMostBorrowedBooks();
    }

    public ServiceResult getMostReadAuthors() {
        return transactionController.getMostReadAuthors();
    }

    public ServiceResult searchBorrowHistoryByTitle(String title) {
        ServiceResult historyResult = transactionController.getBorrowHistory();
        if (!historyResult.isSuccess()) {
            return historyResult;
        }

        @SuppressWarnings("unchecked")
        ArrayList<BorrowOperation> allRecords = (ArrayList<BorrowOperation>) historyResult.getData();
        ArrayList<BorrowOperation> filtered = new ArrayList<>();
        String q = title == null ? "" : title.toLowerCase();
        for (BorrowOperation record : allRecords) {
            if (record.getBook().getTitle() != null && record.getBook().getTitle().toLowerCase().contains(q)) {
                filtered.add(record);
            }
        }

        if (filtered.isEmpty()) {
            return new ServiceResult(false, "No records found");
        }

        return new ServiceResult(true, "Records found", filtered);
    }
}