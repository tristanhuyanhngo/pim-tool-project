package org.elca.neosis.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.elca.neosis.proto.ProjectServiceGrpc;
import org.elca.neosis.proto.GroupServiceGrpc;

public class Grpc {
    private final int PORT = 6565;
    private final GroupServiceGrpc.GroupServiceBlockingStub groupServiceStub;
    private final ProjectServiceGrpc.ProjectServiceBlockingStub projectServiceStub;
    private static Grpc client;

    private Grpc() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", PORT).usePlaintext().build();
        groupServiceStub = GroupServiceGrpc.newBlockingStub(channel);
        projectServiceStub = ProjectServiceGrpc.newBlockingStub(channel);
    }

    public static Grpc getInstance() {
        if (client == null) {
            client = new Grpc();
        }
        return client;
    }

    public ProjectServiceGrpc.ProjectServiceBlockingStub getProjectServiceStub() {
        return projectServiceStub;
    }

    public GroupServiceGrpc.GroupServiceBlockingStub getGroupServiceStub() {
        return groupServiceStub;
    }
}
