/**
 * Author:   xiongkai
 * Date:     2019/7/8 20:12
 */
package com.netty.demo.springbootnetty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 自定义编码器
 */
@Slf4j
public class CustomizeDecoder extends ByteToMessageDecoder {

    private final boolean failFast = false;
    private final boolean stripDelimiter = true;
    /** True if we're discarding input because we're already over maxLength.  */
    private boolean discarding;
    private int discardedBytes;
    /** Last scan position. */
    private int offset;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)  {

        ByteBuf duplicate = in.retainedDuplicate();
        ByteBuf duplicate2 = null;

        in.skipBytes(in.readableBytes());

        /*ByteBuf byteBuf = null;
        try {
            byteBuf = decode(ctx, in);
            if (byteBuf != null) {
                out.add(byteBuf.toString(Charset.defaultCharset()));
            }
            ReferenceCountUtil.release(byteBuf);
            //ReferenceCountUtil.release(in);
        } catch (Exception e) {
            log.error("CustomizeDecoder-decode", e);
            if (byteBuf != null) {
                ReferenceCountUtil.release(byteBuf);
            }
        }*/

    }

    protected ByteBuf decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        final int eol = findEndOfLine(buffer);
        if (!discarding) {
            if (eol >= 0) {
                final ByteBuf frame;
                final int length = eol - buffer.readerIndex();
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;

                if (length > 4096) {
                    buffer.readerIndex(eol + delimLength);
                    fail(ctx, length);
                    return null;
                }

                if (stripDelimiter) {
                    frame = buffer.readRetainedSlice(length);
                    buffer.skipBytes(delimLength);
                } else {
                    frame = buffer.readRetainedSlice(length + delimLength);
                }

                return frame;
            } else {
                final int length = buffer.readableBytes();
                if (length > 4096) {
                    discardedBytes = length;
                    buffer.readerIndex(buffer.writerIndex());
                    discarding = true;
                    offset = 0;
                    if (failFast) {
                        fail(ctx, "over " + discardedBytes);
                    }
                }
                return null;
            }
        } else {
            if (eol >= 0) {
                final int length = discardedBytes + eol - buffer.readerIndex();
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;
                buffer.readerIndex(eol + delimLength);
                discardedBytes = 0;
                discarding = false;
                if (!failFast) {
                    fail(ctx, length);
                }
            } else {
                discardedBytes += buffer.readableBytes();
                buffer.readerIndex(buffer.writerIndex());
                // We skip everything in the buffer, we need to set the offset to 0 again.
                offset = 0;
            }
            return null;
        }
    }

    /**
     * Returns the index in the buffer of the end of line found.
     * Returns -1 if no end of line was found in the buffer.
     */
    private int findEndOfLine(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        //System.out.println(totalLength);
        int i = buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, ByteProcessor.FIND_LF);
        if (i >= 0) {
            offset = 0;
            if (i > 0 && buffer.getByte(i - 1) == '\r') {
                i--;
            }
        } else {
            offset = totalLength;
        }
        return i;
    }

    private int findLineBreak(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        return buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, ByteProcessor.FIND_LF);
    }

    private void fail(final ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }

    private void fail(final ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(
                new TooLongFrameException(
                        "frame length (" + length + ") exceeds the allowed maximum (" + 4096 + ')'));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("--------数据读异常----------:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        //log.info("--------数据读取完毕---------:");
    }


}
