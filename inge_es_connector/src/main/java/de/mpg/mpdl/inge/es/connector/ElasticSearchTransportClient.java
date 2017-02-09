package de.mpg.mpdl.inge.es.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * ElasticSearchTransportClient enables elasticsearch accessibility
 * 
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public enum ElasticSearchTransportClient {

  INSTANCE;

  private ElasticSearchTransportClient() {}

  TransportClient client = null;
  ObjectMapper mapper = null;

  public ObjectMapper getMapper() {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }

  /**
   * get a {@link TransportClient} with predefined {@link Settings}
   * 
   * @return {@link TransportClient}
   */
  protected TransportClient getClient() {

    Settings settings =
        Settings.settingsBuilder()
            .put("cluster.name", PropertyReader.getProperty("es_cluster_name"))
            .put("client.transport.sniff", true).build();
    client = new TransportClient.Builder().settings(settings).build();
    String transportIps = PropertyReader.getProperty("es_transport_ips");
    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        client
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addr), port));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    return client;
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String index(String indexName, String indexType, String id, byte[] voAsBytes) {

    Client client = getClient();
    try {
      IndexResponse indexResponse =
          client.prepareIndex().setIndex(indexName).setType(indexType).setId(id)
              .setSource(voAsBytes).get();
      return indexResponse.getId();
    } finally {
      client.close();
    }
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link ValueObject}
   */
  public byte[] get(String indexName, String indexType, String id) {
    Client client = getClient();
    GetResponse getResponse =
        client.prepareGet().setIndex(indexName).setType(indexType).setId(id).get();
    try {
      byte[] voAsBytes = getResponse.getSourceAsBytes();
      return voAsBytes;
    } finally {
      client.close();
    }
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String update(String indexName, String indexType, String id, byte[] voAsBytes) {
    Client client = getClient();
    try {
      UpdateResponse updateResponse =
          client.prepareUpdate().setIndex(indexName).setType(indexType).setId(id).setDoc(voAsBytes)
              .get();
      return Long.toString(updateResponse.getVersion());
    } finally {
      client.close();
    }
  }

  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @return {@link String}
   */
  public String delete(String indexName, String indexType, String id) {
    Client client = getClient();
    try {
      DeleteResponse deleteResponse =
          client.prepareDelete().setIndex(indexName).setType(indexType).setId(id).get();
      client.close();
      return deleteResponse.getId();
    } finally {
      client.close();
    }
  }
}
