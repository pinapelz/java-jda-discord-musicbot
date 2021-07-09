
import com.darkprograms.speech.translator.GoogleTranslate;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BotTool extends ListenerAdapter{

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        JDA jda = e.getJDA();
        Message message = e.getMessage();
        String msg = message.getContentDisplay();
        if (msg.startsWith("!setplaying")) {
            boolean allowChange = checkAdmin(e);
            if (allowChange) {
                String playingMessage = msg.replaceAll("!setplaying", "");
                System.out.println(returnTimestamp() + " Request to change playing message received");
                jda.getPresence().setActivity(Activity.playing(playingMessage));
                e.getChannel().sendMessage("Playing Status Succsessfully Changed").queue();
            } else {
                e.getChannel().sendMessage("You have no authority to tell me what to do").queue();
            }
        }

        else if(msg.startsWith("!rebuildPlaylist")){
            e.getChannel().sendMessage("Updating the Hololive Music List. Bot will be unusable during this time").queue();
            jda.getPresence().setActivity(Activity.playing("Currently in Maintenance... Updating"));
            boolean allowChange= checkAdmin(e);
            if (allowChange) {

            }
            else{
                e.getChannel().sendMessage("You have no authority to tell me what to do").queue();
            }
        }
        else if (msg.startsWith("!setwatching")) {
          boolean allowChange= checkAdmin(e);
            if (allowChange) {
                String watchingMessage = msg.replaceAll("!setwatching", "");
                System.out.println(returnTimestamp() + " Request to change watching message received");
                jda.getPresence().setActivity(Activity.watching(watchingMessage));
                e.getChannel().sendMessage("Watching Status Succsessfully Changed").queue();
            } else {
                e.getChannel().sendMessage("You have no authority to tell me what to do").queue();
            }
        }

        else if(msg.equals("!maintenance on")){
            boolean allowChange= checkAdmin(e);
            if (allowChange) {
                e.getChannel().sendMessage("The Bot is now in Maintenance Mode").queue();
                jda.shutdown();
            } else {
                e.getChannel().sendMessage("You have no authority to tell me what to do").queue();
            }
        }
        else if(msg.startsWith("!announce")){
            String sendText = msg.replaceAll("!announce","");
            boolean allowChange= checkAdmin(e);
            if (allowChange) {
                e.getChannel().sendMessage(sendText).queue();
                jda.shutdown();
            } else {
                e.getChannel().sendMessage("You have no authority to tell me what to do").queue();
            }
        }
        else if(msg.equals("!help")){
            MessageBuilder helpMessage = new MessageBuilder().setEmbeds(new EmbedBuilder()
                    .setTitle("Holobot !help available commands", "https://github.com/pinapelz/holoDiscord")
                    .setDescription("A list of available commands v.1.8b")
                    .setColor(new Color(8877218))
                    .addField("!hl all [timezone]", "Shows the recent streams of \nHolostars and Hololive members", true)
                    .addField("!hl [index] [timezone]", "Show more info about a stream after \ngetting the index from !hl all [timezone]", true)
                    .addField("!hl upcoming", "Shows upcoming Hololive and Holostars Streams", false)
                    .addField("!holoen [timezone]", "Shows recent streams for only Hololive English members", true)
                    .addField("!hlranking", "Ranks Hololive members by subscribers", false)
                    .addField("!musichelp", "Music Bot Commmands", false)
                    .build());
            e.getChannel().sendMessage(helpMessage.build()).queue();

        }
        else if(msg.equals("!musichelp")||msg.equals("!mhelp")){
            MessageBuilder helpMessage = new MessageBuilder().setEmbeds(new EmbedBuilder()
                    .setTitle("Holobot available music commands", "https://github.com/pinapelz/holoDiscord")
                    .setDescription("A list of available commands v.1.8b")
                    .setColor(new Color(8877218))
                    .addField("!play [url]", "Plays song from YouTube URL", true)
                    .addField("!splay [keywords]", "Searches for keywords on YouTube and plays top search result", true)
                    .addField("!pause", "Pause/Resume the music", true)
                    .addField("!stop", "Stops the music bot an clears the queue", true)
                    .addField("!leave", "Leaves the voice channel", false)
                    .addField("!list", "Lists the songs in the queue", false)
                    .addField("!nowplaying", "Shows the current song playing", false)
                    .build());
            e.getChannel().sendMessage(helpMessage.build()).queue();
        }
        else if(msg.startsWith("!jptoen")){
            String translateText = msg.replaceAll("!jptoen","");
            try {
                System.out.println("Requested translations for: " + translateText + " by " + e.getAuthor());
                String translation = GoogleTranslate.translate("ja","en", translateText);
                e.getChannel().sendMessage(translation).queue();
            } catch (Exception ex) {

            }
        }



    }
    public static String returnTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }
    public boolean checkAdmin(MessageReceivedEvent e){
        Role admin = e.getGuild().getRoleById(returnAdminRole());
        for (int i = 0; i < e.getMember().getRoles().size(); i++) {
            if (e.getMember().getRoles().get(i).equals(admin)) {
                return true;
            }
        }
        return false;
    }
    public String returnAdminRole(){
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader("settings//config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jo = (JSONObject) obj;
        return (String) jo.get("adminRole");
    }

}
