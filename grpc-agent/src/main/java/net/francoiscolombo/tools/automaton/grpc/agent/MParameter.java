// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package net.francoiscolombo.tools.automaton.grpc.agent;

/**
 * Protobuf type {@code agent.MParameter}
 */
public  final class MParameter extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:agent.MParameter)
    MParameterOrBuilder {
  // Use MParameter.newBuilder() to construct.
  private MParameter(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private MParameter() {
    pname_ = "";
    pvalue_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private MParameter(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            pname_ = s;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            pvalue_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return net.francoiscolombo.tools.automaton.grpc.agent.Service.internal_static_agent_MParameter_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return net.francoiscolombo.tools.automaton.grpc.agent.Service.internal_static_agent_MParameter_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            net.francoiscolombo.tools.automaton.grpc.agent.MParameter.class, net.francoiscolombo.tools.automaton.grpc.agent.MParameter.Builder.class);
  }

  public static final int PNAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object pname_;
  /**
   * <code>string pname = 1;</code>
   */
  public java.lang.String getPname() {
    java.lang.Object ref = pname_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      pname_ = s;
      return s;
    }
  }
  /**
   * <code>string pname = 1;</code>
   */
  public com.google.protobuf.ByteString
      getPnameBytes() {
    java.lang.Object ref = pname_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      pname_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int PVALUE_FIELD_NUMBER = 2;
  private volatile java.lang.Object pvalue_;
  /**
   * <code>string pvalue = 2;</code>
   */
  public java.lang.String getPvalue() {
    java.lang.Object ref = pvalue_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      pvalue_ = s;
      return s;
    }
  }
  /**
   * <code>string pvalue = 2;</code>
   */
  public com.google.protobuf.ByteString
      getPvalueBytes() {
    java.lang.Object ref = pvalue_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      pvalue_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getPnameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, pname_);
    }
    if (!getPvalueBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, pvalue_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getPnameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, pname_);
    }
    if (!getPvalueBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, pvalue_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof net.francoiscolombo.tools.automaton.grpc.agent.MParameter)) {
      return super.equals(obj);
    }
    net.francoiscolombo.tools.automaton.grpc.agent.MParameter other = (net.francoiscolombo.tools.automaton.grpc.agent.MParameter) obj;

    boolean result = true;
    result = result && getPname()
        .equals(other.getPname());
    result = result && getPvalue()
        .equals(other.getPvalue());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + PNAME_FIELD_NUMBER;
    hash = (53 * hash) + getPname().hashCode();
    hash = (37 * hash) + PVALUE_FIELD_NUMBER;
    hash = (53 * hash) + getPvalue().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(net.francoiscolombo.tools.automaton.grpc.agent.MParameter prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code agent.MParameter}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:agent.MParameter)
      net.francoiscolombo.tools.automaton.grpc.agent.MParameterOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return net.francoiscolombo.tools.automaton.grpc.agent.Service.internal_static_agent_MParameter_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return net.francoiscolombo.tools.automaton.grpc.agent.Service.internal_static_agent_MParameter_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              net.francoiscolombo.tools.automaton.grpc.agent.MParameter.class, net.francoiscolombo.tools.automaton.grpc.agent.MParameter.Builder.class);
    }

    // Construct using net.francoiscolombo.tools.automaton.grpc.agent.MParameter.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      pname_ = "";

      pvalue_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return net.francoiscolombo.tools.automaton.grpc.agent.Service.internal_static_agent_MParameter_descriptor;
    }

    public net.francoiscolombo.tools.automaton.grpc.agent.MParameter getDefaultInstanceForType() {
      return net.francoiscolombo.tools.automaton.grpc.agent.MParameter.getDefaultInstance();
    }

    public net.francoiscolombo.tools.automaton.grpc.agent.MParameter build() {
      net.francoiscolombo.tools.automaton.grpc.agent.MParameter result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public net.francoiscolombo.tools.automaton.grpc.agent.MParameter buildPartial() {
      net.francoiscolombo.tools.automaton.grpc.agent.MParameter result = new net.francoiscolombo.tools.automaton.grpc.agent.MParameter(this);
      result.pname_ = pname_;
      result.pvalue_ = pvalue_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof net.francoiscolombo.tools.automaton.grpc.agent.MParameter) {
        return mergeFrom((net.francoiscolombo.tools.automaton.grpc.agent.MParameter)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(net.francoiscolombo.tools.automaton.grpc.agent.MParameter other) {
      if (other == net.francoiscolombo.tools.automaton.grpc.agent.MParameter.getDefaultInstance()) return this;
      if (!other.getPname().isEmpty()) {
        pname_ = other.pname_;
        onChanged();
      }
      if (!other.getPvalue().isEmpty()) {
        pvalue_ = other.pvalue_;
        onChanged();
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      net.francoiscolombo.tools.automaton.grpc.agent.MParameter parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (net.francoiscolombo.tools.automaton.grpc.agent.MParameter) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object pname_ = "";
    /**
     * <code>string pname = 1;</code>
     */
    public java.lang.String getPname() {
      java.lang.Object ref = pname_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        pname_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string pname = 1;</code>
     */
    public com.google.protobuf.ByteString
        getPnameBytes() {
      java.lang.Object ref = pname_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        pname_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string pname = 1;</code>
     */
    public Builder setPname(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      pname_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string pname = 1;</code>
     */
    public Builder clearPname() {
      
      pname_ = getDefaultInstance().getPname();
      onChanged();
      return this;
    }
    /**
     * <code>string pname = 1;</code>
     */
    public Builder setPnameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      pname_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object pvalue_ = "";
    /**
     * <code>string pvalue = 2;</code>
     */
    public java.lang.String getPvalue() {
      java.lang.Object ref = pvalue_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        pvalue_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string pvalue = 2;</code>
     */
    public com.google.protobuf.ByteString
        getPvalueBytes() {
      java.lang.Object ref = pvalue_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        pvalue_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string pvalue = 2;</code>
     */
    public Builder setPvalue(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      pvalue_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string pvalue = 2;</code>
     */
    public Builder clearPvalue() {
      
      pvalue_ = getDefaultInstance().getPvalue();
      onChanged();
      return this;
    }
    /**
     * <code>string pvalue = 2;</code>
     */
    public Builder setPvalueBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      pvalue_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:agent.MParameter)
  }

  // @@protoc_insertion_point(class_scope:agent.MParameter)
  private static final net.francoiscolombo.tools.automaton.grpc.agent.MParameter DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new net.francoiscolombo.tools.automaton.grpc.agent.MParameter();
  }

  public static net.francoiscolombo.tools.automaton.grpc.agent.MParameter getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MParameter>
      PARSER = new com.google.protobuf.AbstractParser<MParameter>() {
    public MParameter parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new MParameter(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<MParameter> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MParameter> getParserForType() {
    return PARSER;
  }

  public net.francoiscolombo.tools.automaton.grpc.agent.MParameter getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

