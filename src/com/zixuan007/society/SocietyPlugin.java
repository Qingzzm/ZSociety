package com.zixuan007.society;

import cn.nukkit.command.Command;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.zixuan007.society.command.MainCommand;
import com.zixuan007.society.command.MarryCommand;
import com.zixuan007.society.command.TitleCommand;
import com.zixuan007.society.domain.Society;
import com.zixuan007.society.listener.MarryListener;
import com.zixuan007.society.listener.ResponseLister;
import com.zixuan007.society.listener.SocietyListener;
import com.zixuan007.society.listener.TitleListener;
import com.zixuan007.society.task.BottomTask;
import com.zixuan007.society.utils.MarryUtils;
import com.zixuan007.society.utils.PluginUtils;
import com.zixuan007.society.utils.SocietyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SocietyPlugin extends PluginBase {

    private Config config;
    private List<Config> societyConfigList = new ArrayList<>();
    private Config titleConfig;
    private Config LangConfig;
    private Config marryConfig;
    private Config titleShopConfig;
    private static SocietyPlugin instance;
    private ArrayList<Society> societies = new ArrayList<>();

    public void onEnable() {
        this.init();
        this.getLogger().info("§2公会插件开启 §c作者§f:§bzixuan007");
    }

    public void onDisable() {
        this.getLogger().info("§2公会插件关闭 §c数据保存中...");
        this.societies.forEach((society) -> { society.saveData(); });
    }

    /**
     * 初始化插件数据
     */
    public void init() {
        checkPlugin("EconomyAPI");
        checkPlugin("FloatingText");
        if (instance == null) instance = this;
        checkConfig();
        saveResource("cn_language.yml", true);
        registerCommand();
        loadConfig();
        PluginUtils.getLang();

        if (this.config.getBoolean("是否开启底部", false)) {
            getServer().getScheduler().scheduleRepeatingTask(new BottomTask(this), 10);
        }
        getServer().getPluginManager().registerEvents(new ResponseLister(), this);
        getServer().getPluginManager().registerEvents(new SocietyListener(this), this);
        getServer().getPluginManager().registerEvents(new TitleListener(this), this);
        getServer().getPluginManager().registerEvents(new MarryListener(this), this);
    }

    /**
     * 注册插件命令
     */
    public void registerCommand() {
        getServer().getCommandMap().register("society", (Command) new MainCommand(), "公会");
        getServer().getCommandMap().register("title", (Command) new TitleCommand(), "称号");
        getServer().getCommandMap().register("title", (Command) new MarryCommand(), "结婚");
    }



    /**
     * 加载配置文件
     */
    public void loadConfig() {
        String titleConfigPath = PluginUtils.CONFIGFOLDER + "Title.yml";
        String language = (String) config.get("language");
        String langPath = PluginUtils.CONFIGFOLDER +language+"_language.yml";
        String titleShopPath = PluginUtils.CONFIGFOLDER + "TitleShopData.yml";
        String marryPath=PluginUtils.CONFIGFOLDER+"Marry.yml";

        this.titleConfig = new Config(titleConfigPath);
        this.LangConfig = new Config(langPath);
        this.titleShopConfig = new Config(titleShopPath);
        this.marryConfig=new Config(marryPath);
        MarryUtils.loadMarryConfig();
        SocietyUtils.loadSocietyConfig();

    }

    /**
     * 检测插件
     * @param pluginName 插件名称
     */
    public void checkPlugin(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            getLogger().error("§c检测到 §b" + pluginName + " §c插件不存在,请先安装");
            getServer().shutdown();
        }
    }

    /**
     * 检测主配置文件的版本是否相同
     */
    public void checkConfig() {
        String path = PluginUtils.CONFIGFOLDER + "Config.yml";
        File file = new File(path);
        if (!file.exists()) {
            saveResource("Config.yml");
        } else {
            Config config = new Config(file, 2);
            String version = (String) config.get("version");
            String pluginVersion = getDescription().getVersion();
            if (version == null || !version.equals(pluginVersion)) {
                saveResource("Config.yml", true);
                getLogger().info("§c检测到配置文件版本太低,自动进行覆盖");
            } else {
                saveResource("Config.yml");
            }
        }
        String configPath = PluginUtils.CONFIGFOLDER + "Config.yml";
        this.config = new Config(configPath);
    }



    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static SocietyPlugin getInstance() {
        return instance;
    }

    public static void setInstance(SocietyPlugin instance) {
        SocietyPlugin.instance = instance;
    }

    public ArrayList<Society> getSocieties() {
        return this.societies;
    }

    public void setSocieties(ArrayList<Society> societies) {
        this.societies = societies;
    }

    public Config getTitleConfig() {
        return this.titleConfig;
    }

    public void setTitleConfig(Config titleConfig) {
        this.titleConfig = titleConfig;
    }

    public Config getLangConfig() {
        return this.LangConfig;
    }

    public void setLangConfig(Config langConfig) {
        this.LangConfig = langConfig;
    }

    public List<Config> getSocietyConfigList() {
        return this.societyConfigList;
    }

    public void setSocietyConfigList(List<Config> societyConfigList) {
        this.societyConfigList = societyConfigList;
    }

    public Config getTitleShopConfig() {
        return this.titleShopConfig;
    }

    public void setTitleShopConfig(Config titleShopConfig) {
        this.titleShopConfig = titleShopConfig;
    }

    public Config getMarryConfig() {
        return marryConfig;
    }

    public void setMarryConfig(Config marryConfig) {
        this.marryConfig = marryConfig;
    }
}