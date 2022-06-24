package br.com.zenix.hungergames.player.gamer.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.zenix.core.spigot.player.item.ItemBuilder;
import br.com.zenix.hungergames.game.custom.HungerListener;
import br.com.zenix.hungergames.game.handler.item.CacheItems;
import br.com.zenix.hungergames.player.gamer.Gamer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class GamerSpectateListener extends HungerListener {

	private static final ArrayList<UUID> cantTouch = new ArrayList<>();

	@EventHandler
	public void onRight(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Vehicle
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		} else if (event.getRightClicked() instanceof Player
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			if (event.getPlayer().getItemInHand().getType() == Material.AIR) {
				Player player = (Player) event.getRightClicked();
				Player p = event.getPlayer();
				p.chat("/invsee " + player.getName());
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (cantTouch.contains(event.getWhoClicked().getUniqueId())) {
			if (event.getWhoClicked().hasPermission("hunger.staff")) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventory(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("Jogadores")) {
			event.setCancelled(true);

			Player p = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();

			if (item != null && item.getType() == Material.SKULL_ITEM && item.getDurability() == 3 && item.hasItemMeta()
					&& item.getItemMeta().hasDisplayName()) {
				String display = item.getItemMeta().getDisplayName();
				Player player = null;

				for (Gamer g : getManager().getGamerManager().getGamers().values()) {
					if (g.isAlive()) {
						Player o = g.getAccount().getPlayer();
						if (ChatColor.stripColor(o.getName()).equalsIgnoreCase(ChatColor.stripColor(display))) {
							player = o;
							break;
						}
					}
				}

				p.closeInventory();

				if (player == null) {
					p.sendMessage("§cEste jogador morreu ou saiu do servidor!");
				} else {
					p.teleport(player.getLocation());
					p.sendMessage("§aTeleportado para §f" + display);
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (cantTouch.contains(event.getPlayer().getUniqueId())) {
			cantTouch.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (getManager().getGamerManager().getGamer(player).isSpectating()) {
			if (event.getWhoClicked().hasPermission("hunger.staff")) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player
				&& getManager().getGamerManager().getGamer(event.getEntity().getUniqueId()) != null
				&& getManager().getGamerManager().getGamer(event.getEntity().getUniqueId()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player
				&& getManager().getGamerManager().getGamer((Player) event.getEntity()).isSpectating()) {
			event.setCancelled(true);
			event.setFoodLevel(20);
		}
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player p = (Player) event.getTarget();
			if (p.hasPermission("hunger.staff")) {
				return;
			}
			if (getManager().getGamerManager().getGamer(p).isSpectating()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			Block b = event.getClickedBlock();

			if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper
					|| b.getState() instanceof Dispenser || b.getState() instanceof Furnace
					|| b.getState() instanceof Beacon) {
				if (!event.getPlayer().hasPermission("hunger.staff")) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.hasPermission("hunger.staffplus")) {
				return;
			} else if (getManager().getGamerManager().getGamer(player).isSpectating()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission("hunger.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("hunger.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().hasPermission("hunger.staff")) {
			return;
		}
		if (getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()) {
			event.setCancelled(true);
		}
	}

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (getManager().getGamerManager().getGamer(event.getPlayer()) != null
				&& getManager().getGamerManager().getGamer(event.getPlayer()).isSpectating()
				&& event.getAction().name().contains("RIGHT")) {
			if (itemBuilder.checkItem(event.getItem(),
					CacheItems.SPEC.getItem(0).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				Inventory inventory = Bukkit.createInventory(event.getPlayer(), 54, "Jogadores");

				int slot = 0;
				for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
					if (g.getPlayer().isOnline()) {
						if (slot <= 50)
							inventory.setItem(slot++,
									new SkullConstructor(g.getAccount().getPlayer().getName(),
											g.getAccount().getPlayer().getName(),
											Arrays.asList("§aKit: §f" + g.getKit().getName(),
													"§aKills: §f" + g.getGameKills(), " ",
													"§eClique para teleportar-se!")).toItemStack());
					}
				}

				event.getPlayer().openInventory(inventory);

			} else if (itemBuilder.checkItem(event.getItem(),
					CacheItems.SPEC.getItem(1).getStack().getItemMeta().getDisplayName())) {
				event.setCancelled(true);

				List<Gamer> pvp = new ArrayList<>();
				for (Gamer g : getManager().getGamerManager().getAliveGamers()) {
					if (g.isFighting() && g.isAlive()) {
						pvp.add(g);
					}
				}

				if (pvp.size() == 0) {
					event.getPlayer().sendMessage("§cNenhum player está em combate.");
					return;
				}

				Gamer random = pvp.get(getManager().getRandom().nextInt(pvp.size()));
				event.getPlayer().teleport(random.getPlayer().getLocation());
				event.getPlayer().sendMessage("§aTeleportado para §f" + random.getPlayer().getName());
				return;
			}
		}
	}

	public static class SkullConstructor {

		private String _name;
		private String displayName;
		private List<String> lore;

		public SkullConstructor(String name, String displayName, List<String> lore) {
			this._name = name;
			this.displayName = displayName;
			this.lore = lore;
		}

		public ItemStack toItemStack() {
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(this.displayName);
			itemMeta.setLore(this.lore);

			GameProfile profile = (GameProfile) ReflectionUtils.searchUUID(this._name);
			PropertyMap propertyMap = profile.getProperties();

			if (propertyMap != null) {
				Class<?> itemMetaClass = itemMeta.getClass();
				ReflectionUtils.replaceField(itemMetaClass, itemMeta, "profile", profile);
				item.setItemMeta(itemMeta);
			} else {
				item.setItemMeta(itemMeta);
			}

			return item;
		}
	}

	public static class ReflectionUtils {

		public static class OBCCraftItemStack {

			public static Class<?> getOBCClass() {
				return ReflectionUtils
						.getClassByName(ReflectionUtils.getOBCPackageName() + ".inventory.CraftItemStack");
			}

			public static ItemStack asBukkitCopy(Object nmsItemStack) {
				try {
					Method m = getOBCClass().getDeclaredMethod("asBukkitCopy",
							ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".ItemStack"));
					m.setAccessible(true);
					return (ItemStack) m.invoke(null, nmsItemStack);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public static Object asNMSCopy(ItemStack stack) {
				try {
					Method m = getOBCClass().getDeclaredMethod("asNMSCopy", ItemStack.class);
					m.setAccessible(true);
					return m.invoke(null, stack);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		public static class NMSMerchantRecipeList {
			private Object handle;

			public static Class<?> getNMSClass() {
				return ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".MerchantRecipeList");
			}

			public NMSMerchantRecipeList() {
				try {
					this.handle = getNMSClass().newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public NMSMerchantRecipeList(Object handle) {
				this.handle = handle;
			}

			public Object getHandle() {
				return this.handle;
			}

			public void clear() {
				try {
					Method m = ArrayList.class.getDeclaredMethod("clear");
					m.setAccessible(true);
					m.invoke(this.handle);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void add(NMSMerchantRecipe recipe) {
				try {
					Method m = ArrayList.class.getDeclaredMethod("add", Object.class);
					m.setAccessible(true);
					m.invoke(this.handle, recipe.getMerchantRecipe());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public List<NMSMerchantRecipe> getRecipes() {
				List<NMSMerchantRecipe> recipeList = new ArrayList<NMSMerchantRecipe>();
				for (Object obj : (List<?>) handle) {
					recipeList.add(new NMSMerchantRecipe(obj));
				}
				return recipeList;
			}
		}

		public static class NMSMerchantRecipe {
			private Object merchantRecipe;

			public NMSMerchantRecipe(Object merchantRecipe) {
				this.merchantRecipe = merchantRecipe;
			}

			public NMSMerchantRecipe(Object item1, Object item3) {
				this(item1, null, item3);
			}

			public NMSMerchantRecipe(Object item1, Object item2, Object item3) {
				try {
					Class<?> isClass = ReflectionUtils
							.getClassByName(ReflectionUtils.getNMSPackageName() + ".ItemStack");
					this.merchantRecipe = getNMSClass().getDeclaredConstructor(isClass, isClass, isClass)
							.newInstance(item1, item2, item3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public static Class<?> getNMSClass() {
				return ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".MerchantRecipe");
			}

			public Object getBuyItem1() {
				try {
					Method m = getNMSClass().getDeclaredMethod("getBuyItem1");
					m.setAccessible(true);
					return m.invoke(this.merchantRecipe);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public Object getBuyItem2() {
				try {
					Method m = getNMSClass().getDeclaredMethod("getBuyItem2");
					m.setAccessible(true);
					return m.invoke(this.merchantRecipe);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public Object getBuyItem3() {
				try {
					Method m = getNMSClass().getDeclaredMethod("getBuyItem3");
					m.setAccessible(true);
					return m.invoke(this.merchantRecipe);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			public int getMaxUses() {
				try {
					Field field = getNMSClass().getDeclaredField("maxUses");
					field.setAccessible(true);
					return (int) field.getByte(this.merchantRecipe);
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
			}

			public void setMaxUses(int maxUses) {
				try {
					Field field = getNMSClass().getDeclaredField("maxUses");
					field.setAccessible(true);
					field.set(this.merchantRecipe, maxUses);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public Object getMerchantRecipe() {
				return this.merchantRecipe;
			}

		}

		public static class PlayerInfoAction {
			public static Object UPDATE_GAME_MODE = getNMSAction("UPDATE_GAME_MODE");
			public static Object ADD_PLAYER = getNMSAction("ADD_PLAYER");
			public static Object UPDATE_DISPLAY_NAME = getNMSAction("UPDATE_DISPLAY_NAME");
			public static Object REMOVE_PLAYER = getNMSAction("REMOVE_PLAYER");
			private static Class<?> nmsClass;

			private static Object getNMSAction(String name) {
				try {
					Field field = getNMSClass().getDeclaredField(name);
					field.setAccessible(true);
					return field.get(null);
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}

			@SuppressWarnings("rawtypes")
			public static Class getNMSClass() {
				if (nmsClass == null) {
					nmsClass = getClassByName(getNMSPackageName() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction"); // Spigot
																														// 1.8.3
					if (nmsClass == null) {
						nmsClass = getClassByName(getNMSPackageName() + ".EnumPlayerInfoAction");
					}
				}
				return nmsClass;
			}
		}

		@SuppressWarnings("deprecation")
		public static Object createNMSGameMode(GameMode gameMode) {
			Class<?> c = getClassByName(getNMSPackageName() + ".EnumGamemode");
			if (c == null) {
				c = getClassByName(getNMSPackageName() + ".WorldSettings$EnumGamemode");
			}

			try {
				Method method = c.getDeclaredMethod("getById", int.class);
				method.setAccessible(true);
				return method.invoke(null, gameMode.getValue());
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static Object createPlayerInfoData(Object gameProfile, GameMode gameMode, int ping, String nickName) {
			Class<?> playerInfoDataClass = getClassByName(
					getNMSPackageName() + ".PacketPlayOutPlayerInfo$PlayerInfoData");

			if (playerInfoDataClass == null) {
				playerInfoDataClass = getClassByName(getNMSPackageName() + ".PlayerInfoData");
			}

			Object nmsGameMode = createNMSGameMode(gameMode);

			try {
				Constructor<?> constructor = playerInfoDataClass.getDeclaredConstructor(
						getClassByName(getNMSPackageName() + ".PacketPlayOutPlayerInfo"),
						getClassByName("com.mojang.authlib.GameProfile"), int.class, nmsGameMode.getClass(),
						getClassByName(getNMSPackageName() + ".IChatBaseComponent"));
				constructor.setAccessible(true);
				return constructor.newInstance(null, gameProfile, ping, nmsGameMode, createNMSTextComponent(nickName));
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static Object fillProfileProperties(Object gameProfile) {
			Class<?> serverClass = getClassByName(getNMSPackageName() + ".MinecraftServer");
			Class<?> sessionServiceClass = getClassByName("com.mojang.authlib.minecraft.MinecraftSessionService");

			try {
				Object minecraftServer;
				{
					Method method = serverClass.getDeclaredMethod("getServer");
					method.setAccessible(true);
					minecraftServer = method.invoke(null);
				}

				Object sessionService;
				{
					String methodName;
					if (existsMethod(serverClass, "aC", sessionServiceClass))
						methodName = "aC"; // 1.8.3
					else
						methodName = "aD"; // 1.8.8
					Method method = serverClass.getDeclaredMethod(methodName);
					method.setAccessible(true);
					sessionService = method.invoke(minecraftServer);
				}

				Object result;
				{
					Method method = sessionServiceClass.getDeclaredMethod("fillProfileProperties",
							gameProfile.getClass(), boolean.class);
					method.setAccessible(true);
					result = method.invoke(sessionService, gameProfile, true);
				}

				return result;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		private static boolean existsMethod(Class<?> clazz, String methodName, Class<?> returnClass) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getName().equals(methodName) && method.getGenericReturnType() == returnClass) {
					return true;
				}
			}
			return false;
		}

		/** Return: GameProfile */
		public static Object searchUUID(String playerName) {
			Class<?> serverClass = getClassByName(getNMSPackageName() + ".MinecraftServer");
			Class<?> userCacheClass = getClassByName(getNMSPackageName() + ".UserCache");

			try {
				Object minecraftServer;
				{
					Method method = serverClass.getDeclaredMethod("getServer");
					method.setAccessible(true);
					minecraftServer = method.invoke(null);
				}

				Object userCache;
				{
					Method method = serverClass.getDeclaredMethod("getUserCache");
					method.setAccessible(true);
					userCache = method.invoke(minecraftServer);
				}

				{
					Method method = userCacheClass.getDeclaredMethod("getProfile", String.class);
					method.setAccessible(true);
					return method.invoke(userCache, playerName);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static Object createNMSTextComponent(String text) {
			if (text == null || text.isEmpty()) {
				return null;
			}

			Class<?> c = getClassByName(getNMSPackageName() + ".ChatComponentText");
			try {
				Constructor<?> constructor = c.getDeclaredConstructor(String.class);
				return constructor.newInstance(text);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static Object toEntityHuman(Player player) {
			try {
				Class<?> c = getClassByName(getOBCPackageName() + ".entity.CraftPlayer");
				Method m = c.getDeclaredMethod("getHandle");
				m.setAccessible(true);
				return m.invoke(player);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static Class<?> getClassByName(String name) {
			try {
				return Class.forName(name);
			} catch (Exception e) {
				return null;
			}
		}

		public static Object getField(Class<?> c, Object obj, String key) throws Exception {
			Field field = c.getDeclaredField(key);
			field.setAccessible(true);
			return field.get(obj);
		}

		public static void replaceField(Class<?> c, Object obj, String key, Object value) {
			try {
				Field field = c.getDeclaredField(key);
				field.setAccessible(true);
				field.set(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static String getNMSPackageName() {
			return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		}

		public static String getOBCPackageName() {
			return "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		}
	}
}
