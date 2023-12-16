import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CryptoInfoApp {

    private static final String API_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=BTC,ETH,LTC";
    private static final String API_KEY = "27ab17d1-215f-49e5-9ca4-afd48810c149";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CoinRich");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new GridLayout(6, 1));

        JLabel headingLabel = new JLabel("CoinRich", JLabel.CENTER);
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));

        frame.add(headingLabel);

        String[] coinSymbols = {"Cardano", "Cosmos", "Bitcoin Cash", "BNB", "Bitcoin"};

        for (String coinSymbol : coinSymbols) {
            JPanel coinPanel = createCoinPanel(coinSymbol);
            frame.add(coinPanel);
        }

        frame.setVisible(true);
    }

    private static JPanel createCoinPanel(String coinSymbol) {
        JPanel coinPanel = new JPanel();
        coinPanel.setBackground(Color.BLACK);
        coinPanel.setLayout(new GridLayout(4, 1));

        JLabel nameLabel = new JLabel(coinSymbol, JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel priceLabel = new JLabel("Price: -", JLabel.CENTER);
        priceLabel.setForeground(Color.WHITE);

        JLabel rankLabel = new JLabel("CMC Rank: -", JLabel.CENTER);
        rankLabel.setForeground(Color.WHITE);

        JLabel symbolLabel = new JLabel("Symbol: " + coinSymbol, JLabel.CENTER);
        symbolLabel.setForeground(Color.WHITE);

        JButton fetchDataButton = new JButton("Fetch Data");
        fetchDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String data = fetchDataFromAPI(coinSymbol);
                updateLabels(data, priceLabel, rankLabel);
            }
        });

        coinPanel.add(nameLabel);
        coinPanel.add(priceLabel);
        coinPanel.add(rankLabel);
        coinPanel.add(symbolLabel);
        coinPanel.add(fetchDataButton);

        return coinPanel;
    }

    private static String fetchDataFromAPI(String coinSymbol) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-CMC_PRO_API_KEY", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                throw new RuntimeException("Failed to fetch crypto data. HTTP Error Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateLabels(String data, JLabel priceLabel, JLabel rankLabel) {
        if (data != null) {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
                JsonObject btcData = jsonObject.getAsJsonObject("data").getAsJsonObject("BTC").getAsJsonObject("quote").getAsJsonObject("USD");

                String price = btcData.get("price").getAsString();
                String rank = btcData.get("cmc_rank").getAsString();

                priceLabel.setText("Price: $" + price);
                rankLabel.setText("CMC Rank: " + rank);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            priceLabel.setText("Price: N/A");
            rankLabel.setText("CMC Rank: N/A");
        }
    }
}
