package library_manage.controller;

import java.util.ArrayList;
import library_manage.Model.BorrowOperation;
import library_manage.util.ServiceResult;

public class ReportController {
// here i deal with the transaction controller because i don't have service for report but i have service for transaction 
// so i use the transaction controller to get the data from the transaction service and then i filter it and return it to the report panel
    private final TransactionController transactionController;

    public ReportController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    // WE USING THIS CLASS FROM CLASS  ReportPanel IN  refreshReport() ,  in searchByTitle() in searchBorrowHistoryByUserName() . THEN   TO CLASS  TransactionController
    // WE USING THIS CLASS FROM CLASS  StatisticsPanel IN  refreshStatistics() . THEN   TO CLASS  TransactionController

    public ServiceResult getBorrowHistory() { // we using this in class ReportPanel in refreshReport() when we want to refresh the borrow history table
        return transactionController.getBorrowHistory();
    }

    public ServiceResult getCurrentBorrowedBooks() { // we usign this in class ReportPanel in refreshReport() when we want to refresh the number of current borrowed books table
        return transactionController.getCurrentBorrowedBooks();
    }

    public ServiceResult getMostBorrowedBooks() { // we using in class StatisticsPanel in refreshStatistics() and in class ReportPanel in refreshReport() for displaying most borrowed books
        return transactionController.getMostBorrowedBooks();
    }

    public ServiceResult getMostReadAuthors() { // we using in class StatisticsPanel in refreshStatistics() and in class ReportPanel in refreshReport() for displaying most read authors
        return transactionController.getMostReadAuthors();
    }

    public ServiceResult searchBorrowHistoryByTitle(String title) { // we using in class ReportPanel in searchByTitle() when we want to search for a borrow history by book title from the report panel
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

    public ServiceResult searchBorrowHistoryByUserName(String name) { // this using in class ReportPanel in searchBorrowHistoryByUserName() when we want to search for a borrow history by user name from the report panel
        return transactionController.searchBorrowHistoryByUserName(name);
    }
}