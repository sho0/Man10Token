package man10token.man10token;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Random;


public final class Man10token extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("enabled man10 token gen");
        this.saveDefaultConfig();
        configReload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("disabled man10 token gen");
    }

    String mysqlip = this.getConfig().getString("server_config.mysql_ip");
    String mysport = this.getConfig().getString("server_config.mysql_port");
    String mysqlid = this.getConfig().getString("server_config.mysql_id");
    String mysqlpass = this.getConfig().getString("server_config.mysql_pass");
    String dbname = this.getConfig().getString("server_config.db_name");
    String ccset = this.getConfig().getString("generator_option.enable_custom_char_set.charset");
    String tableinfo = this.getConfig().getString("server_config.table_info");
    int tokenL = this.getConfig().getInt("generator_option.length");

    boolean customcharset = this.getConfig().getBoolean("generator_option.enable_custom_char_set.enable");
    boolean useatoz = this.getConfig().getBoolean("generator_option.char_setting.use.a_to_z");
    boolean useAtoZ = this.getConfig().getBoolean("generator_option.char_setting.use.A_to_Z");
    boolean use0to9 = this.getConfig().getBoolean("generator_option.char_setting.use.0_to_9");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("tokengen")){//適当
            String token = createToken();
            p.sendMessage("§2**************[掲示板トークンジェネレーター]*****************");
            p.sendMessage("");
            p.sendMessage("§2あなたのトークンは: " + token);
            p.sendMessage("");
            p.sendMessage("§2**********************************************************");
            sendMySql(p.getUniqueId().toString(), token,p);
        }else if(cmd.getName().equalsIgnoreCase("tokengenreload")){
            configReload();
            p.sendMessage("config reloaded");
        }
        return true;
    }

    public void sendMySql(String uuid, String token,Player p){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + mysqlip + "/" + dbname + "?autoReconnect=true&useSSL=false", mysqlid, mysqlpass);
            Statement st = conn.createStatement();

            String search = "SELECT * FROM " + tableinfo + " WHERE uuid ='" + uuid + "'" ;
            PreparedStatement s = conn.prepareStatement(search);
            ResultSet r = s.executeQuery();

                String sql = "INSERT INTO " + tableinfo + " " +
                        "VALUES ('" + uuid + "', '" + token + "')";
                st.executeUpdate(sql);
                st.close();
                conn.close();
            } catch(ClassNotFoundException e){
                System.out.println("ドライバを読み込めませんでした " + e);
            } catch(SQLException e){
                System.out.println("データベース接続エラー" + e);
            }

    }

    public String createToken(){
        String cset = "";
        Random r = new Random();
        if(customcharset == true){
            return generateString(r, ccset, tokenL);
        }else{
            if(useatoz == true){
                cset = cset + "abcdefghijklmnopqrstuvwxyz";
            }
            if(useAtoZ == true){
                cset = cset + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            }
            if(use0to9 == true){
                cset = cset + "0123456789";
            }

        }
        return generateString(r,cset,tokenL);
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);

    }
    
    public void configReload(){
        this.reloadConfig();
        mysqlip = this.getConfig().getString("server_config.mysql_ip");
        mysport = this.getConfig().getString("server_config.mysql_port");
        mysqlid = this.getConfig().getString("server_config.mysql_id");
        mysqlpass = this.getConfig().getString("server_config.mysql_pass");
        dbname = this.getConfig().getString("server_config.db_name");
        tokenL = this.getConfig().getInt("generator_option.length");
        ccset = this.getConfig().getString("generator_option.enable_custom_char_set.charset");
        tableinfo = this.getConfig().getString("server_config.table_info");

        customcharset = this.getConfig().getBoolean("generator_option.enable_custom_char_set.enable");
        useatoz = this.getConfig().getBoolean("generator_option.char_setting.use.a_to_z");
        useAtoZ = this.getConfig().getBoolean("generator_option.char_setting.use.A_to_Z");
        use0to9 = this.getConfig().getBoolean("generator_option.char_setting.use.0_to_9");
        getLogger().info("config reloaded");
        return;
    }
}
