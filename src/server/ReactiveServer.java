package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactiveServer {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel
                serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new
                InetSocketAddress("localhost", 4444));
        int ops = serverSocketChannel.validOps();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int select = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    handleAccept(selector, serverSocketChannel, selectionKey);
                } else if (selectionKey.isReadable()) {
                    handleRead(selector, selectionKey);
                }
                iterator.remove();
            }
        }
    }
    private static void handleRead(Selector selector,SelectionKey selectionKey)
            throws IOException, IOException {
        System.out.println("Reading new Message ");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        socketChannel.read(byteBuffer);
        String request=new String(byteBuffer.array()).trim();
        System.out.println(request);
        if(request.length()>0){
            System.out.println("Received Message => "+request);
            if(request.equals("exit")){
                socketChannel.close();
                System.out.println("Connection closed");
            }
        }
        String response="Request Size : "+request.length();
        ByteBuffer byteBufferResponse=ByteBuffer.allocate(1024);
        byteBufferResponse.put(response.getBytes());
        byteBufferResponse.flip();
        int writtenBytes = socketChannel.write(byteBufferResponse);
        System.out.println("sending "+writtenBytes+" bytes");
    }
    private static void handleAccept(Selector selector, ServerSocketChannel
            serverSocketChannel, SelectionKey selectionKey) throws IOException {
        System.out.println("Accept New Connection");
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
    }
}

