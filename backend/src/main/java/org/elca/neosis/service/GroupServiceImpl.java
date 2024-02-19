package org.elca.neosis.service;

import io.grpc.stub.StreamObserver;
import org.elca.neosis.proto.GroupRequest;
import org.elca.neosis.proto.GroupResponse;
import org.elca.neosis.proto.GroupServiceGrpc;
import org.elca.neosis.repository.GroupRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@GRpcService
public class GroupServiceImpl extends GroupServiceGrpc.GroupServiceImplBase {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void getAllGroupIDs(GroupRequest request, StreamObserver<GroupResponse> responseObserver) {
        List<Long> groupIDs = groupRepository.getAllGroupIDs();
        responseObserver.onNext(GroupResponse.newBuilder().addAllId(groupIDs).build());
        responseObserver.onCompleted();
    }
}
