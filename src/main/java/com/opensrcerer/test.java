package com.opensrcerer;

import com.opensrcerer.requestEntities.TokenInfo;

import javax.security.auth.login.LoginException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class test {
    public static void main(String[] args) throws InterruptedException, ExecutionException, LoginException {
        BTJ btj = BTJ.getBTJ("mI7d.x7XsEJuatLSGYAd7RdBH"); // Initialize BTJ with Token

        // -------- Synchronous Test ---------
        TokenInfo info = btj.getInfo().complete();
        System.out.println(info.getUses());
        System.out.println(info.getLimit());
        System.out.println(info.getNextReset());
        // ------------------------------------


        // -------- Asynchronous Test (Promise) ---------
        CompletableFuture<TokenInfo> info1 = btj.getInfo().submit();
        Thread.sleep(1000);
        if (info1.isDone()) {
            TokenInfo info2 = info1.get();
            System.out.println(info2.getUses());
            System.out.println(info2.getLimit());
            System.out.println(info2.getNextReset());
        }
        // ------------------------------------


        // -------- Asynchronous Test (Callback) ---------
        btj.getInfo().queue(tokenInfo -> {
            System.out.println(tokenInfo.getUses());
            System.out.println(tokenInfo.getLimit());
            System.out.println(tokenInfo.getNextReset());
        });
        // ------------------------------------
    }
}
