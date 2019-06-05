package xx.mix.bootes.kinako.transport;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SocksHttpsTransportTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	@Ignore
	public void test() throws Exception {
		SocksHttpsTransport service = new SocksHttpsTransport("localhost", 9050);
		CloseableHttpClient httpClient = service.createHttpClient();
		try {
			CloseableHttpResponse response = httpClient.execute(new HttpGet("https://translations.telegram.org/"));
			try {
				System.out.println("------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				System.out.println(EntityUtils.toString(entity, "UTF-8"));
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} finally {
			IOUtils.closeQuietly(httpClient);
		}
		
		//fail("Not yet implemented");
	}

}
