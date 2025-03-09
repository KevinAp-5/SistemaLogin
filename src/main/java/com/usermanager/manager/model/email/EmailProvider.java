package com.usermanager.manager.model.email;

import java.util.concurrent.CompletableFuture;

public interface EmailProvider {
    CompletableFuture<Void> sendEmail(String recipient, String subject, String content);
}
