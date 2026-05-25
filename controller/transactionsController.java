package library_manage.controller;

import library_manage.Model.*;
import library_manage.util.*;
import library_manage.services.*;

public class transactionsController {
    private BorrowServices borrowService;
    private UserServices userServices;
    public transactionsController(BorrowServices borrowService) {
        this.borrowService = borrowService;
    }

    public String borrowBook(String name, String isbn) {
        User user = userServices.searchUserByName(name);
        ServiceResult result = borrowService.borrowBook(user, isbn);
        return result.getMessage();
    }

    public String returnBook(String name, String isbn) {
        User user = userServices.searchUserByName(name);
        ServiceResult result = borrowService.returnBook(user, isbn);
        return result.getMessage();
    }
}
