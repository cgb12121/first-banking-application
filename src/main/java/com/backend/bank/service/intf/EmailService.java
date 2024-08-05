package com.backend.bank.service.intf;

import com.backend.bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailOnSignUp(EmailDetails emailDetails);

    void sendEmailOnTransaction(EmailDetails emailDetails);

    void sendEmailOnLogin(EmailDetails emailDetails);

    void sendEmailOnResetPassword(EmailDetails emailDetails);

    void sendEmailOnAccountStatus(EmailDetails emailDetails);

    void sendEmailOnAddNewCard(EmailDetails emailDetails);

    void sendEmailOnUpgradeAccountType(EmailDetails emailDetails);

    void sendEmailOnTakingLoan(EmailDetails emailDetails);

    void sendEmailOnLoanIncreasedInterest(EmailDetails emailDetails);

    void sendEmailOnLoanDecreasedInterest(EmailDetails emailDetails);

    void sendEmailOnLoanLate(EmailDetails emailDetails);

    void sendEmailOnLoanPaid(EmailDetails emailDetails);
}
