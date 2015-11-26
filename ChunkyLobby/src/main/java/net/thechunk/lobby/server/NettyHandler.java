package net.thechunk.lobby.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.Lobby;
import net.thechunk.lobby.data.jsonobjects.ServerRequest;
import net.thechunk.lobby.data.jsonobjects.ServerResponse;

class NettyHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        ServerRequest sr = ChunkyLobby.getPlugin().getGson().fromJson(message, ServerRequest.class);
        Lobby lobby;
        switch (sr.getAction()) {
            case GET_LOBBY_FOR_PLAYER:
                if (sr.getArgs().length < 1) {
                    ctx.channel().writeAndFlush(createError("Not enough arguments supplied."));
                    break;
                }
                String response = ChunkyLobby.getPlugin().getLobbyDestinationFor(sr.getArgs()[0]);
                if (response != null) {
                    ctx.channel().writeAndFlush(createResponse(response));
                } else {
                    ctx.channel().writeAndFlush(createError("No such lobby was found."));
                }
                break;
            case LOBBY_INFO:
                if (sr.getArgs().length < 1) {
                    ctx.channel().writeAndFlush(createError("Not enough arguments supplied."));
                    break;
                }
                lobby = ChunkyLobby.getPlugin().getLobbies().get(sr.getArgs()[0]);
                if (lobby != null) {
                    ctx.channel().writeAndFlush(toJson(new ServerResponse(ServerResponse.Status.OK,
                            new String[]{lobby.getName(), lobby.getWorld()})));
                } else {
                    ctx.channel().writeAndFlush(createError("No such lobby was found."));
                }
                break;
            case JOINED_LOBBY:
                // This is just for bookkeeping. No need to reply.
                if (sr.getArgs().length < 1) {
                    break;
                }
                lobby = ChunkyLobby.getPlugin().getLobbies().get(sr.getArgs()[0]);
                if (lobby != null) {
                    if (!lobby.getPlayers().contains(sr.getArgs()[0])) {
                        lobby.addPlaying(sr.getArgs()[1]);
                        ChunkyLobby.getPlugin().updateLobby(sr.getArgs()[1], lobby);
                    }
                }
                break;
            case LEFT_LOBBY:
                // This is just for bookkeeping. No need to reply.
                if (sr.getArgs().length < 1) {
                    break;
                }
                lobby = null;
                for (Lobby lobby1 : ChunkyLobby.getPlugin().getLobbies().values()) {
                    if (lobby1.hasPlayer(sr.getArgs()[0])) {
                        lobby = lobby1;
                        break;
                    }
                }
                if (lobby != null) {
                    lobby.removePlaying(sr.getArgs()[1]);
                    ChunkyLobby.getPlugin().updateLobby(sr.getArgs()[0], lobby);
                }
                break;
            case UPDATE_LOBBY_STATUS:
                if (sr.getArgs().length < 2) {
                    ctx.channel().writeAndFlush(createError("Not enough arguments supplied."));
                    break;
                }
                lobby = ChunkyLobby.getPlugin().getLobbies().get(sr.getArgs()[0]);
                if (lobby != null) {
                    switch (sr.getArgs()[1]) {
                        case "s1":
                            lobby.setStatus1(sr.getArgs()[1]);
                            ChunkyLobby.getPlugin().updateLobby(sr.getArgs()[0], lobby);
                            ctx.channel().writeAndFlush(createResponse("Lobby updated."));
                            break;
                        case "s2":
                            lobby.setStatus1(sr.getArgs()[1]);
                            ChunkyLobby.getPlugin().updateLobby(sr.getArgs()[0], lobby);
                            ctx.channel().writeAndFlush(createResponse("Lobby updated."));
                            break;
                        default:
                            ctx.channel().writeAndFlush(createError("No such property exists."));
                            break;
                    }
                } else {
                    ctx.channel().writeAndFlush(createError("No such lobby exists."));
                }
                break;
            case GET_HUB_SERVER:
                ctx.channel().writeAndFlush(createResponse("lobby"));
                break;
            case PING:
                ctx.channel().writeAndFlush(createResponse("Pong!"));
                break;
            default:
                ctx.channel().writeAndFlush(createError("No such command exists."));
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().writeAndFlush(createError("An internal error occurred... why are you here?"));
        ctx.close();
    }

    private String createError(String reason) {
        return toJson(new ServerResponse(ServerResponse.Status.ERROR, new String[]{reason}));
    }

    private String createResponse(String reply) {
        return toJson(new ServerResponse(ServerResponse.Status.OK, new String[]{reply}));
    }

    private String toJson(ServerResponse reply) {
        return ChunkyLobby.getPlugin().getGson().toJson(reply);
    }
}