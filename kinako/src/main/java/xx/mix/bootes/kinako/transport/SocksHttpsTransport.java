package xx.mix.bootes.kinako.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

public class SocksHttpsTransport {
	
	static class FakeDnsResolver implements DnsResolver {

		@Override
		public InetAddress[] resolve(String host) throws UnknownHostException {
			return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1}) };
		}
		
	}
	
	static class PlainSocketFactory extends PlainConnectionSocketFactory {
		private final InetSocketAddress socksAddr;
		
		public PlainSocketFactory(InetSocketAddress socks_addr) {
			this.socksAddr = socks_addr;
		}
		
		@Override
		public Socket createSocket(HttpContext context) throws IOException {
			return new Socket(new Proxy(Proxy.Type.SOCKS, socksAddr));
		}
		
		@Override
		public Socket connectSocket(int connect_timeout,
									Socket socket,
									HttpHost host,
									InetSocketAddress remote_address,
									InetSocketAddress local_address,
									HttpContext context)
			throws IOException
		{
			InetSocketAddress unresolv_remote = InetSocketAddress
				.createUnresolved(host.getHostName(), remote_address.getPort());
			return super.connectSocket(
					connect_timeout,
					socket,
					host,
					unresolv_remote,
					local_address,
					context
				);
		}
		
	}
	
	static class SslSocketFactory extends SSLConnectionSocketFactory {
		private final InetSocketAddress socksAddr;
		
		public SslSocketFactory(InetSocketAddress socks_addr, SSLContext context) {
			super(context);
			this.socksAddr = socks_addr;
		}
		
		@Override
		public Socket createSocket(HttpContext context) throws IOException {
			return new Socket(new Proxy(Proxy.Type.SOCKS, socksAddr));
		}
		
		@Override
		public Socket connectSocket(int connect_timeout,
									Socket socket,
									HttpHost host,
									InetSocketAddress remote_address,
									InetSocketAddress local_address,
									HttpContext context)
			throws IOException
		{
			InetSocketAddress unresolv_remote = InetSocketAddress
				.createUnresolved(host.getHostName(), remote_address.getPort());
			return super.connectSocket(
					connect_timeout,
					socket,
					host,
					unresolv_remote,
					local_address,
					context
				);
		}
	
	}
	
	private final InetSocketAddress socksAddr;
	
	public SocksHttpsTransport(InetSocketAddress socks_addr) {
		this.socksAddr = socks_addr;
	}
	
	public SocksHttpsTransport(String socks_host, int socks_port) {
		this(new InetSocketAddress(socks_host, socks_port));
	}
	
	public CloseableHttpClient createHttpClient() {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", new PlainSocketFactory(socksAddr))
				.register("https", new SslSocketFactory(socksAddr, SSLContexts.createSystemDefault()))
				.build();
		return HttpClients.custom()
			.setConnectionManager(new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver()))
			.build();
	}

}
