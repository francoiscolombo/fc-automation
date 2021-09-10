package net.francoiscolombo.tools.automaton.grpc.agent;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: service.proto")
public final class AgentServiceGrpc {

  private AgentServiceGrpc() {}

  public static final String SERVICE_NAME = "agent.AgentService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook,
      net.francoiscolombo.tools.automaton.grpc.agent.MResponse> METHOD_PLAY =
      io.grpc.MethodDescriptor.<net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook, net.francoiscolombo.tools.automaton.grpc.agent.MResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "agent.AgentService", "play"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.francoiscolombo.tools.automaton.grpc.agent.MPing,
      net.francoiscolombo.tools.automaton.grpc.agent.MPong> METHOD_PING =
      io.grpc.MethodDescriptor.<net.francoiscolombo.tools.automaton.grpc.agent.MPing, net.francoiscolombo.tools.automaton.grpc.agent.MPong>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "agent.AgentService", "ping"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MPing.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MPong.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest,
      net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse> METHOD_SENDFILE =
      io.grpc.MethodDescriptor.<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest, net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "agent.AgentService", "sendfile"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AgentServiceStub newStub(io.grpc.Channel channel) {
    return new AgentServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AgentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new AgentServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AgentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new AgentServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class AgentServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void play(net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook request,
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PLAY, responseObserver);
    }

    /**
     */
    public void ping(net.francoiscolombo.tools.automaton.grpc.agent.MPing request,
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MPong> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PING, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest> sendfile(
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_SENDFILE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PLAY,
            asyncUnaryCall(
              new MethodHandlers<
                net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook,
                net.francoiscolombo.tools.automaton.grpc.agent.MResponse>(
                  this, METHODID_PLAY)))
          .addMethod(
            METHOD_PING,
            asyncUnaryCall(
              new MethodHandlers<
                net.francoiscolombo.tools.automaton.grpc.agent.MPing,
                net.francoiscolombo.tools.automaton.grpc.agent.MPong>(
                  this, METHODID_PING)))
          .addMethod(
            METHOD_SENDFILE,
            asyncClientStreamingCall(
              new MethodHandlers<
                net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest,
                net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse>(
                  this, METHODID_SENDFILE)))
          .build();
    }
  }

  /**
   */
  public static final class AgentServiceStub extends io.grpc.stub.AbstractStub<AgentServiceStub> {
    private AgentServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentServiceStub(channel, callOptions);
    }

    /**
     */
    public void play(net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook request,
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PLAY, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(net.francoiscolombo.tools.automaton.grpc.agent.MPing request,
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MPong> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileRequest> sendfile(
        io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_SENDFILE, getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class AgentServiceBlockingStub extends io.grpc.stub.AbstractStub<AgentServiceBlockingStub> {
    private AgentServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public net.francoiscolombo.tools.automaton.grpc.agent.MResponse play(net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PLAY, getCallOptions(), request);
    }

    /**
     */
    public net.francoiscolombo.tools.automaton.grpc.agent.MPong ping(net.francoiscolombo.tools.automaton.grpc.agent.MPing request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PING, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AgentServiceFutureStub extends io.grpc.stub.AbstractStub<AgentServiceFutureStub> {
    private AgentServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private AgentServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AgentServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AgentServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.francoiscolombo.tools.automaton.grpc.agent.MResponse> play(
        net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PLAY, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.francoiscolombo.tools.automaton.grpc.agent.MPong> ping(
        net.francoiscolombo.tools.automaton.grpc.agent.MPing request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request);
    }
  }

  private static final int METHODID_PLAY = 0;
  private static final int METHODID_PING = 1;
  private static final int METHODID_SENDFILE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AgentServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AgentServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PLAY:
          serviceImpl.play((net.francoiscolombo.tools.automaton.grpc.agent.MPlaybook) request,
              (io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MResponse>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((net.francoiscolombo.tools.automaton.grpc.agent.MPing) request,
              (io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MPong>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SENDFILE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sendfile(
              (io.grpc.stub.StreamObserver<net.francoiscolombo.tools.automaton.grpc.agent.MSendFileResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class AgentServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return net.francoiscolombo.tools.automaton.grpc.agent.Service.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AgentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AgentServiceDescriptorSupplier())
              .addMethod(METHOD_PLAY)
              .addMethod(METHOD_PING)
              .addMethod(METHOD_SENDFILE)
              .build();
        }
      }
    }
    return result;
  }
}
