package io.dhoom.util;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import net.minecraft.server.v1_8_R3.*;
import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.*;

public class UtilActionMessage
{
    private List<AMText> Text;
    
    public UtilActionMessage() {
        this.Text = new ArrayList<AMText>();
    }
    
    public AMText addText(final String Message) {
        final AMText Text = new AMText(Message);
        this.Text.add(Text);
        return Text;
    }
    
    public String getFormattedMessage() {
        String Chat2 = "[\"\",";
        for (final AMText Text : this.Text) {
            Chat2 = Chat2 + Text.getFormattedMessage() + ",";
        }
        Chat2 = Chat2.substring(0, Chat2.length() - 1);
        Chat2 += "]";
        return Chat2;
    }
    
    public void sendToPlayer(final Player Player2) {
        final IChatBaseComponent base = IChatBaseComponent.ChatSerializer.a(this.getFormattedMessage());
        final PacketPlayOutChat packet = new PacketPlayOutChat(base, (byte)1);
        ((CraftPlayer)Player2).getHandle().playerConnection.sendPacket((Packet)packet);
    }
    
    public enum ClickableType
    {
        RunCommand("run_command"), 
        SuggestCommand("suggest_command"), 
        OpenURL("open_url");
        
        public String Action;
        
        private ClickableType(final String Action2) {
            this.Action = Action2;
        }
    }
    
    public class AMText
    {
        private String Message;
        private Map<String, Map.Entry<String, String>> Modifiers;
        
        public AMText(final String Text) {
            this.Message = "";
            this.Modifiers = new HashMap<String, Map.Entry<String, String>>();
            this.Message = Text;
        }
        
        public String getMessage() {
            return this.Message;
        }
        
        public String getFormattedMessage() {
            String Chat2 = "{\"text\":\"" + this.Message + "\"";
            for (final String Event2 : this.Modifiers.keySet()) {
                final Map.Entry<String, String> Modifier = this.Modifiers.get(Event2);
                Chat2 = Chat2 + ",\"" + Event2 + "\":{\"action\":\"" + Modifier.getKey() + "\",\"value\":" + Modifier.getValue() + "}";
            }
            Chat2 += "}";
            return Chat2;
        }
        
        public AMText addHoverText(final String... Text) {
            final String Event2 = "hoverEvent";
            final String Key = "show_text";
            String Value = "";
            if (Text.length == 1) {
                Value = "{\"text\":\"" + Text[0] + "\"}";
            }
            else {
                Value = "{\"text\":\"\",\"extra\":[";
                for (final String Message : Text) {
                    Value = Value + "{\"text\":\"" + Message + "\"},";
                }
                Value = Value.substring(0, Value.length() - 1);
                Value += "]}";
            }
            final AbstractMap.SimpleEntry<String, String> Values2 = new AbstractMap.SimpleEntry<String, String>("show_text", Value);
            this.Modifiers.put("hoverEvent", Values2);
            return this;
        }
        
        public AMText addHoverItem(final ItemStack Item2) {
            final String Event2 = "hoverEvent";
            final String Key = "show_item";
            final String Value = CraftItemStack.asNMSCopy(Item2).getTag().toString();
            final AbstractMap.SimpleEntry<String, String> Values2 = new AbstractMap.SimpleEntry<String, String>("show_item", Value);
            this.Modifiers.put("hoverEvent", Values2);
            return this;
        }
        
        public AMText setClickEvent(final ClickableType Type2, final String Value) {
            final String Event2 = "clickEvent";
            final String Key = Type2.Action;
            final AbstractMap.SimpleEntry<String, String> Values2 = new AbstractMap.SimpleEntry<String, String>(Key, "\"" + Value + "\"");
            this.Modifiers.put("clickEvent", Values2);
            return this;
        }
    }
}
