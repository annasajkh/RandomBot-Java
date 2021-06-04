import com.github.annasajkh.Api;
import twitter4j.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main
{
    static Twitter twitter;

    public static String generateTweet(Api api) throws IOException, InterruptedException, TwitterException
    {
        if (api.type == Api.Type.IMAGE)
        {
            Image image = null;
            try
            {
                URL url = new URL(api.link);
                image = ImageIO.read(url);
            }
            catch (Exception exception)
            {
                return "";
            }
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics()
                         .drawImage(image, 0, 0, null);
            ImageIO.write(bufferedImage, "PNG", new File("img.png"));
            StatusUpdate statusUpdate = new StatusUpdate("");
            statusUpdate.setMedia(new File("img.png"));
            twitter.updateStatus(statusUpdate);
            return "";
        }

        //get content of the web
        JSONObject jsonObject = null;
        if (api.type != Api.Type.JOKE)
        {
            URL url = new URL(api.link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
            {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            String string = String.valueOf(content);
            if (api.type == Api.Type.MOTIVATION)
            {
                string = string.replace("[", "")
                               .replace("]", "");
            }
            jsonObject = new JSONObject(string);
        }
        else
        {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("https://joke3.p.rapidapi.com/v1/joke"))
                                             .header("x-rapidapi-key", "-")
                                             .header("x-rapidapi-host", "joke3.p.rapidapi.com")
                                             .method("GET", HttpRequest.BodyPublishers.noBody())
                                             .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                                                      .send(request, HttpResponse.BodyHandlers.ofString());
            jsonObject = new JSONObject(response.body());
            return jsonObject.getString("content");
        }

        //filter content
        if (api.type == Api.Type.MOTIVATION)
        {
            return jsonObject.getString("q") + "\n- " + jsonObject.getString("a");
        }
        else if (api.type == Api.Type.ADVICE)
        {
            return jsonObject.getJSONObject("slip")
                             .getString("advice");
        }
        else if (api.type == Api.Type.QUOTE)
        {
            return jsonObject.getJSONObject("quote")
                             .getString("quoteText") +
                   "\n- " +
                   jsonObject.getJSONObject("quote")
                             .getString("quoteAuthor");
        }
        return "";
    }


    public static void main(String[] args) throws IOException, InterruptedException, TwitterException
    {
        twitter = TwitterFactory.getSingleton();
        List<String> objects = new ArrayList<>();
        List<String> followerNames;
        List<String> alreadyTweetedFollowers = new ArrayList<>();
        String object;
        BufferedReader fileReader = new BufferedReader(new FileReader("objects.txt"));
        while ((object = fileReader.readLine()) != null)
        {
            objects.add(object);
        }
        fileReader.close();
        object = null;
        Random random = new Random();
        int index = 0;
        Api[] apis = {new Api("https://zenquotes.io/api/random", Api.Type.MOTIVATION),
                      new Api("", Api.Type.JOKE),
                      new Api("https://quoteimg.glitch.me/generate", Api.Type.IMAGE),
                      new Api("https://api.adviceslip.com/advice", Api.Type.ADVICE),
                      new Api("https://quote-garden.herokuapp.com/api/v2/quotes/random", Api.Type.QUOTE)};
        String[] weekdays = {"monday", "tuesday", "wednesday", "thursday", "friday"};
        /*
        name -> random follower name
        object -> random object
        weekday -> random weekday
        random -> random num between 0,1
        */
        //Hallowen Code
        //                IDs ids = twitter.getFollowersIDs(-1);
        //                do
        //                {
        //                    for (long userID : ids.getIDs())
        //                    {
        //                        User user = twitter.showUser(userID);
        //                        twitter.sendDirectMessage(user.getScreenName(), "Happy Halloween " +
        //                                                                        user.getName() +
        //                                                                        " have a spook-tacular, boo-tiful,wooo-nderful, and fang-tasti Halloween!" +
        //                                                                        "\nhttps://media1.tenor.com/images/760e7c2ae1a4e60dc5188b99b3093b79/tenor.gif?itemid=3563371");
        //                        TimeUnit.SECONDS.sleep(3);
        //                    }
        //                } while (ids.hasNext());
        String[] sentences = {"*hug name* :D",
                              "lol! throws object at name",
                              "name is staying past their bedtime",
                              "one object makes its way into name bathtub",
                              "name bed has hidden object",
                              "The probability of name receiving one object as a birthday gift is random",
                              "one object a day keeps the name away",
                              "on weekdays,name likes to buy a new object",
                              "on weekdays, name likes to gift out a new object"};
        while (true)
        {
            try
            {
                String tweet;

                if (index == 5)
                {
                    followerNames = new ArrayList<>();

                    IDs ids = twitter.getFollowersIDs(-1);
                    do
                    {
                        for (long userID : ids.getIDs())
                        {
                            followerNames.add(twitter.showUser(userID)
                                                     .getName());
                        }
                    } while (ids.hasNext());
                    String randomNum = String.valueOf(random.nextFloat());
                    if (randomNum.length() >= 5)
                    {
                        randomNum = randomNum.substring(0, 5);
                    }

                    String choosedFollower = followerNames.get(random.nextInt(followerNames.size()));
                    if (alreadyTweetedFollowers.size() == followerNames.size())
                    {
                        alreadyTweetedFollowers.clear();
                    }
                    while (alreadyTweetedFollowers.contains(choosedFollower))
                    {
                        choosedFollower = followerNames.get(random.nextInt(followerNames.size()));
                    }
                    alreadyTweetedFollowers.add(choosedFollower);

                    tweet = sentences[random.nextInt(sentences.length)].replace("name", choosedFollower)
                                                                       .replace("object", objects.get(random.nextInt(objects.size())))
                                                                       .replace("weekday", weekdays[random.nextInt(weekdays.length)])
                                                                       .replace("random", randomNum);

                }
                else
                {
                    tweet = generateTweet(apis[index]);
                }
                if (tweet.length() == 0)
                {
                    TimeUnit.HOURS.sleep(1);
                    index++;
                    continue;
                }

                if (tweet.length() > 280)
                {
                    tweet = generateTweet(apis[index]);
                }

                twitter.updateStatus(tweet);
                index++;
                if (index > apis.length)
                {
                    index = 0;
                }
                TimeUnit.HOURS.sleep(1);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                System.in.read();
                System.exit(-1);
            }
        }
    }
}
