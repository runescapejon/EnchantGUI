package me.tychsen.enchantgui.config;

import me.tychsen.enchantgui.economy.MoneyPayment;
import me.tychsen.enchantgui.economy.NullPayment;
import me.tychsen.enchantgui.economy.PaymentStrategy;
import me.tychsen.enchantgui.economy.XPPayment;
import me.tychsen.enchantgui.localization.LocalizationManager;
import me.tychsen.enchantgui.menu.DefaultMenuSystem;
import me.tychsen.enchantgui.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class EshopConfig {
    private static EshopConfig instance;
    private Main plugin;
    private FileConfiguration config;
    private PaymentStrategy economy;

    public EshopConfig() {
        instance = this;
        this.plugin = Main.getInstance();
        config = plugin.getConfig();
    }

    public boolean getDebug(){
        return config.getBoolean("debug");
    }

    public int getPrice(Enchantment enchantment, int level) {
        String path = enchantment.getKey().toString().toLowerCase() + ".level" + level;
        path = path.split(":")[1];
        return config.getInt(path);
    }

    public String getMenuName() {
        String path = "menu-name";
        if (config.contains(path) && config.isSet(path) && config.isString(path)) {
            if (config.getString(path).length() > 32) {
                return config.getString(path).substring(0, 32);
            } else {
                return config.getString(path);
            }
        } else {
            return "EnchantGUI";
        }
    }

    public void reloadConfig(CommandSender sender) {
        LocalizationManager lm = LocalizationManager.getInstance();
        if (sender.isOp() || sender.hasPermission("eshop.admin")) {
            plugin.reloadConfig();
            config = plugin.getConfig();
            economy = null;
            sender.sendMessage(DefaultMenuSystem.start + lm.getString("config-reloaded"));
        } else {
            sender.sendMessage(DefaultMenuSystem.start + lm.getString("no-permission"));
        }
    }

    public String[] getEnchantLevels(Enchantment ench) {
        String path = ench.getKey().toString().toLowerCase();
        path = path.split(":")[1];
        Main.debug(path);
        Map<String, Object> enchantMap = config.getConfigurationSection(path).getValues(false);
        String[] enchantLevels = new String[enchantMap.size()];

        int position = 0;
        for (Map.Entry<String, Object> entry : enchantMap.entrySet()) {
            enchantLevels[position] = entry.getKey();
            position++;
        }

        return enchantLevels;
    }

    public PaymentStrategy getEconomy() {
        if (economy == null) {
            switch (config.getString("payment-currency").toLowerCase()) {
                case "money":
                    economy = new MoneyPayment();
                    break;
                case "xp":
                    economy = new XPPayment();
                    break;
                default:
                    economy = new NullPayment();
                    break;
            }
        }

        return economy;
    }

    public static EshopConfig getInstance() {
        return instance;
    }
}
