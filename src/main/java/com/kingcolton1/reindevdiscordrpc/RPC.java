package com.kingcolton1.reindevdiscordrpc;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RPC {
    private IPCClient ipcClient = null;

    public void start() {
        if (this.ipcClient != null && this.ipcClient.getStatus() == PipeStatus.CONNECTED) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                IPCClient newClient = new IPCClient(1385147749128867901L);
                newClient.setListener(new IPCListener() {
                    @Override
                    public void onClose(IPCClient client, JsonObject details) {
                        stop();
                    }
                    @Override
                    public void onDisconnect(IPCClient client, Throwable t) {
                        stop();
                    }
                    @Override
                    public void onPacketSent(IPCClient client, Packet packet) {}
                    @Override
                    public void onPacketReceived(IPCClient client, Packet packet) {}

                    @Override
                    public void onActivityJoin(IPCClient client, String secret) {}

                    @Override
                    public void onActivitySpectate(IPCClient client, String secret) {}

                    @Override
                    public void onActivityJoinRequest(IPCClient client, String secret, User user) {}

                    @Override
                    public void onReady(IPCClient client) {}
                });
                newClient.connect();
                this.ipcClient = newClient;
            } catch (NoDiscordClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stop() {
        if (this.ipcClient.getStatus() == PipeStatus.DISCONNECTED) {
            ipcClient = null;
        }
        else {
            this.ipcClient.close();
            this.ipcClient = null;
        }
    }

    public void update(Consumer<RichPresence.Builder> action) {
        if (this.ipcClient != null) {
            RichPresence.Builder builder = new RichPresence.Builder();
            action.accept(builder);
            this.ipcClient.sendRichPresence(builder.build());
        }
    }
}
