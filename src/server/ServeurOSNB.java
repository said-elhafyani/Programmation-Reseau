package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServeurOSNB {
    // Un tampon pour stocker les messages partiels de chaque client
    private static Map<SocketChannel, StringBuffer> clientBuffers = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("0.0.0.0", 4444));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            int channelCount = selector.select();
            if(channelCount == 0){
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    handleAccept(key, selector);
                }else if(key.isReadable()){
                    handleReadWrite(key, selector);
                }
                iterator.remove();
            }
        }
    }

    private static void handleReadWrite(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int dataSize = socketChannel.read(byteBuffer);

        if(dataSize == -1){
            System.out.println(String.format("The client %s has been disconnected", socketChannel.getRemoteAddress()));
            socketChannel.close();
            clientBuffers.remove(socketChannel);
            return;
        }

        byteBuffer.flip();
        StringBuffer messageBuffer = clientBuffers.computeIfAbsent(socketChannel, k -> new StringBuffer());

        while (byteBuffer.hasRemaining()) {
            char ch = (char) byteBuffer.get();
            if (ch == '\n') {
                processMessage(socketChannel, messageBuffer.toString().trim());
                messageBuffer.setLength(0); // Réinitialiser le tampon pour le prochain message
            } else {
                messageBuffer.append(ch);
            }
        }
    }

    private static void processMessage(SocketChannel socketChannel, String message) throws IOException {
        System.out.println(String.format("The client %s received the message %s", socketChannel.getRemoteAddress(), message));
        String response = new StringBuffer(message).reverse().toString();
        ByteBuffer byteBufferResponse = ByteBuffer.allocate(1024);
        byteBufferResponse.put(response.getBytes());
        byteBufferResponse.flip();
        socketChannel.write(byteBufferResponse);
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println(String.format("New Connection from %s", socketChannel.getRemoteAddress()));
    }
}
