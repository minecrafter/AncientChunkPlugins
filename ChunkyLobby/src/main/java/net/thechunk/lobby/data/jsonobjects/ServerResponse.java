package net.thechunk.lobby.data.jsonobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResponse {

    private Status status;
    private String[] reply;

    public enum Status {
        OK,
        ERROR
    }
}
