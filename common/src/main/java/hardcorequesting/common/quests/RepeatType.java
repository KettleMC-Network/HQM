package hardcorequesting.common.quests;

import hardcorequesting.common.client.interfaces.GuiColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;

public enum RepeatType {
    NONE("none", false) {
        @Environment(EnvType.CLIENT)
        @Override
        public String getMessage(Quest quest, Player player, int days, int hours) {
            return null;
        }
    },
    INSTANT("instant", false) {
        @Environment(EnvType.CLIENT)
        @Override
        public String getMessage(Quest quest, Player player, int days, int hours) {
            return super.getMessage(quest, player, days, hours) + GuiColor.GRAY + I18n.get("hqm.repeat.instant.message");
        }
    
        @Environment(EnvType.CLIENT)
        @Override
        public String getShortMessage(int days, int hours) {
            return GuiColor.YELLOW + I18n.get("hqm.repeat.instant.message");
        }
    },
    INTERVAL("interval", true) {
        @Environment(EnvType.CLIENT)
        @Override
        public String getMessage(Quest quest, Player player, int days, int hours) {
            return super.getMessage(quest, player, days, hours) + GuiColor.GRAY + I18n.get("hqm.repeat.interval.message") + "\n" + formatTime(days, hours) + "\n" + formatIntervalTime(quest, player, days, hours);
        }
    
        @Environment(EnvType.CLIENT)
        @Override
        public String getShortMessage(int days, int hours) {
            return GuiColor.YELLOW + I18n.get("hqm.repeat.interval.message") + " (" + days + ":" + hours + ")";
        }
    },
    TIME("time", true) {
        @Environment(EnvType.CLIENT)
        @Override
        public String getMessage(Quest quest, Player player, int days, int hours) {
            return super.getMessage(quest, player, days, hours) + GuiColor.GRAY + I18n.get("hqm.repeat.time.message") + "\n" + formatTime(days, hours) + formatCooldownTime(quest, player, days, hours);
        }
    
        @Environment(EnvType.CLIENT)
        @Override
        public String getShortMessage(int days, int hours) {
            return GuiColor.YELLOW + I18n.get("hqm.repeat.time.message") + " (" + days + ":" + hours + ")";
        }
    };
    
    private String id;
    private boolean useTime;
    
    RepeatType(String id, boolean useTime) {
        this.id = id;
        this.useTime = useTime;
    }
    
    /**
     * Formats and produces text describing the reset time, given the cooldown time from a quest being finished to reset
     */
    @Environment(EnvType.CLIENT)
    private static String formatCooldownTime(Quest quest, Player player, int days, int hours) {
        if (!quest.getQuestData(player).available) {
            int timerDuration = days * 24 + hours;
            long timerStart = quest.getQuestData(player).time;
            long current = Quest.clientTicker.getHours();
            int remaining = (int) (timerStart + timerDuration - current);
            
            return "\n" + formatRemainingTime(quest, player, remaining / 24, remaining % 24);
        } else {
            return "";
        }
    }
    
    /**
     * Formats and produces text describing the reset time, given the interval time between scheduled resets
     */
    @Environment(EnvType.CLIENT)
    private static String formatIntervalTime(Quest quest, Player player, int days, int hours) {
        if (days == 0 && hours == 0) {
            return GuiColor.RED + I18n.get("hqm.repeat.invalid");
        }
        
        int interval = days * 24 + hours;
        int remaining = interval - (int) (Quest.clientTicker.getHours() % interval);
        
        return formatRemainingTime(quest, player, remaining / 24, remaining % 24);
    }
    
    /**
     * Formats and produces text describing the reset time, given the remaining time until the next reset
     */
    @Environment(EnvType.CLIENT)
    private static String formatRemainingTime(Quest quest, Player player, int days, int hours) {
        
        if (!quest.isAvailable(player)) {
            return GuiColor.YELLOW + I18n.get("hqm.repeat.resetIn", formatTime(days, hours));
        } else {
            return GuiColor.GRAY + I18n.get("hqm.repeat.nextReset", formatTime(days, hours));
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static String formatTime(int days, int hours) {
        String str = GuiColor.GRAY.toString();
        if (days > 0) {
            str += GuiColor.LIGHT_GRAY;
        }
        str += days;
        str += " ";
        str += I18n.get("hqm.repeat." + (days == 1 ? "day" : "days"));
        
        str += GuiColor.GRAY;
        
        str += " " + I18n.get("hqm.repeat.and") + " ";
        
        if (hours > 0) {
            str += GuiColor.LIGHT_GRAY;
        }
        
        str += hours;
        str += " ";
        str += I18n.get("hqm.repeat." + (hours == 1 ? "hour" : "hours"));
        
        return str;
    }
    
    @Environment(EnvType.CLIENT)
    public String getName() {
        return I18n.get("hqm.repeat." + id + ".title");
    }
    
    @Environment(EnvType.CLIENT)
    public String getDescription() {
        return I18n.get("hqm.repeat." + id + ".desc");
    }
    
    public boolean isUseTime() {
        return useTime;
    }
    
    @Environment(EnvType.CLIENT)
    public String getMessage(Quest quest, Player player, int days, int hours) {
        return GuiColor.YELLOW + I18n.get("hqm.repeat.repeatable") + "\n";
    }
    
    @Environment(EnvType.CLIENT)
    public String getShortMessage(int days, int hours) {
        return null;
    }
}
