package io.dhoom.scoreboard;

import org.bukkit.*;

public class SidebarEntry
{
    public String prefix;
    public String suffix;
    
    public SidebarEntry(final String input) {
        if (input.length() <= 16) {
            this.prefix = input;
            this.suffix = "";
        }
        else {
            String first = input.substring(0, 16);
            String second = input.substring(16, input.length());
            if (first.endsWith(String.valueOf('�'))) {
                first = first.substring(0, first.length() - 1);
                second = "�" + second;
            }
            final String lastColors = ChatColor.getLastColors(first);
            second = lastColors + second;
            this.prefix = first;
            this.suffix = second;
        }
    }
    
    public SidebarEntry(final String prefix, final String name, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
}
