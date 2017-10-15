package turgutsonmez.com.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

  ListView listView;
  ArrayList<String> xmlList = new ArrayList<>();
  ArrayList<String> xmlLink = new ArrayList<>();
  ArrayList<String> xmlImg = new ArrayList<>();
  Context context = this;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    new arkaPlanIsleri().execute();
    listView = (ListView) findViewById(R.id.list);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //ilk olarak xmlLink çalıştırmamız lazım
        Uri link = Uri.parse(xmlLink.get(position));
        Intent openBrowser = new Intent(Intent.ACTION_VIEW, link);
        startActivity(openBrowser);
      }
    });
  }

  public class myCustomAdapter extends ArrayAdapter<String> {


    public myCustomAdapter(Context context, int textViewResourceId, ArrayList<String> xmlList) {
      super(context, textViewResourceId, xmlList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View row = convertView; //yukarıda kullandığım view-convertView ın adını row olarak değiştirdim
      if (row == null) {
        LayoutInflater layoutInflater = getLayoutInflater();
        row = layoutInflater.inflate(R.layout.list, parent, false);
      }
      TextView textView = row.findViewById(R.id.text);
      textView.setText(xmlList.get(position));
      final ImageView image = row.findViewById(R.id.img);
      new Thread(new Runnable() {
        @Override
        public void run() {
          String imgUrl = xmlImg.get(position);
          try {
            URL url00 = new URL(imgUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url00.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream ınputStream = urlConnection.getInputStream();
            final Bitmap bmp= BitmapFactory.decodeStream(ınputStream);
            image.post(new Runnable() {
              @Override
              public void run() {
               image.setImageBitmap(bmp);
              }
            });

          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).start();
      return row;
    }
  }

  public class arkaPlanIsleri extends AsyncTask<Void, Void, Void> {
    ProgressDialog dialog = new ProgressDialog(context);


    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog.setMessage("Yükleniyor...");
      dialog.show();
    }

    @Override
    protected Void doInBackground(Void... objects) {
      xmlList = getListFromXml("http://www.milliyet.com.tr/rss/rssNew/SonDakikaRss.xml");
      xmlLink = getLinkFromXml("http://www.milliyet.com.tr/rss/rssNew/SonDakikaRss.xml");
      xmlImg = getImgFromXml("http://www.milliyet.com.tr/rss/rssNew/SonDakikaRss.xml");
      return null;
    }

    @Override
    protected void onPostExecute(Void o) {
      super.onPostExecute(o);
      myCustomAdapter adapter = new myCustomAdapter(context, R.layout.list, xmlList);
      listView.setAdapter(adapter);
      dialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
    }

  }

  //sitenin Rss inde itemin içerisindeki title bilgisini çekip arrayliste ekleyelim
  public ArrayList<String> getListFromXml(String strng) {
    ArrayList<String> list = new ArrayList<>();
    try {
      URL url = new URL(strng);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new InputSource(url.openStream()));
      document.getDocumentElement().normalize();

      NodeList nodeList = document.getElementsByTagName("item");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        Element elementMain = (Element) node;
        NodeList nodeListTitle = elementMain.getElementsByTagName("title");
        Element elementTitle = (Element) nodeListTitle.item(0);
        list.add(elementTitle.getChildNodes().item(0).getNodeValue());
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return list;
  }

  public ArrayList<String> getLinkFromXml(String strng) {
    ArrayList<String> list = new ArrayList<>();
    try {
      URL url = new URL(strng);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new InputSource(url.openStream()));
      document.getDocumentElement().normalize();

      NodeList nodeList = document.getElementsByTagName("item");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        Element elementMain = (Element) node;
        NodeList nodeListTitle = elementMain.getElementsByTagName("link");
        Element elementTitle = (Element) nodeListTitle.item(0);
        list.add(elementTitle.getChildNodes().item(0).getNodeValue());
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return list;
  }

  public ArrayList<String> getImgFromXml(String strng) {
    ArrayList<String> list = new ArrayList<>();
    try {
      URL url = new URL(strng);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new InputSource(url.openStream()));
      document.getDocumentElement().normalize();

      NodeList nodeList = document.getElementsByTagName("item");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        Element elementMain = (Element) node;
        NodeList nodeListTitle = elementMain.getElementsByTagName("description");
        Element elementTitle = (Element) nodeListTitle.item(0);
        //list.add(elementTitle.getChildNodes().item(0).getNodeValue());
        String description = elementTitle.getChildNodes().item(0).getNodeValue();
        if (description.contains("<img ")) {
          String img = description.substring(description.indexOf("<img "));
          img = img.substring(img.indexOf("src=") + 5);
          img = img.substring(0, img.lastIndexOf("\""));
          Log.i("gelen değer: ", img);
          list.add(img);
        }
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return list;
  }
}
