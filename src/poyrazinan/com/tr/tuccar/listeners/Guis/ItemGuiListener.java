package poyrazinan.com.tr.tuccar.listeners.Guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import poyrazinan.com.tr.tuccar.Tuccar;
import poyrazinan.com.tr.tuccar.Utils.getLang;
import poyrazinan.com.tr.tuccar.Utils.Storage.ConfirmationStorage;
import poyrazinan.com.tr.tuccar.Utils.Storage.ProductStorage;
import poyrazinan.com.tr.tuccar.database.DatabaseQueries;
import poyrazinan.com.tr.tuccar.gui.ConfirmationGui;
import poyrazinan.com.tr.tuccar.gui.ItemGui;
import poyrazinan.com.tr.tuccar.gui.ItemSelectionGui;

public class ItemGuiListener implements Listener {
	
	@SuppressWarnings("unused")
	private Tuccar plugin;
	public ItemGuiListener(Tuccar plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void itemGuiEvent(InventoryClickEvent e)
	{
		if (e.getView().getTitle().contains(Tuccar.color(getLang.getText("itemGui")))) {
			e.setCancelled(true);
			if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST) {
					Player player = (Player) e.getWhoClicked();
					if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) return;
					
					String[] sayfa = e.getView().getTitle().split(" ");
					String page = sayfa[sayfa.length-1].substring(1, sayfa[sayfa.length-1].length());
					
					 String[] Splitter = sayfa[sayfa.length-2].split("/");
					 String category = Splitter[0];
					 String product = Splitter[1];
					 List<ProductStorage> item = new ArrayList<ProductStorage>();
					 for (ProductStorage pcs : DatabaseQueries.getCategoryItemList(product, category)) {
						 if (pcs.getItemCategory().equalsIgnoreCase(category)) item.add(pcs); else continue;}
					 
					 if (!ItemSelectionListener.isNumeric(page)) return;
					 else {
						 e.setCancelled(true);
						 if (e.getSlot() == 45) {
							 player.closeInventory();
							 ItemSelectionGui.createGui(player, category, 1);
							 return;
						 }
						 if (!item.isEmpty()) {
							 int tiklama = 0;
							 if (e.getSlot() == 53) player.closeInventory();
							 else if (e.getSlot() >= 10 && e.getSlot() <= 43){
								 if (e.getSlot() >= 10 && e.getSlot() <= 16) tiklama = e.getSlot()-10;
								 else if (e.getSlot() >= 19 && e.getSlot() <= 25) tiklama = e.getSlot()-12; // 7
								 else if (e.getSlot() >= 28 && e.getSlot() <= 34) tiklama = e.getSlot()-14; // 14
								 else if (e.getSlot() >= 37 && e.getSlot() <= 43) tiklama = e.getSlot()-16; // 21
								 
								 int tiklamaCalculator = ((Integer.valueOf(page)*28)-28)+tiklama;
								 Material itemStack = Material.getMaterial(item.get(tiklamaCalculator).getItemMaterial().toUpperCase());
								 
								 int buyAmount = 1;
								 
								 if (e.getClick().equals(ClickType.SHIFT_LEFT))
								 {
									  
									 if (Tuccar.instance.getConfig().isSet("Settings.customBuyAmount"))
										 buyAmount = Tuccar.instance.getConfig().getInt("Settings.customBuyAmount");
									 
									 else buyAmount = 32;
									 
									 if (item.get(tiklamaCalculator).getStock() < buyAmount)
										 buyAmount = item.get(tiklamaCalculator).getStock();
									 
								 }
								 
								 else if (e.getClick().equals(ClickType.RIGHT)) {
									 if (item.get(tiklamaCalculator).getStock() >= itemStack.getMaxStackSize()) 
										 buyAmount = itemStack.getMaxStackSize();
									 else buyAmount = item.get(tiklamaCalculator).getStock();
								 } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
									 int slots = ConfirmationGuiListener.getEmptySlotsAmount(player);
									 int maxStackSize = itemStack.getMaxStackSize();
									 int playerMaxSize = slots*maxStackSize;
									 if (playerMaxSize >= item.get(tiklamaCalculator).getStock()) buyAmount = item.get(tiklamaCalculator).getStock();
									 else buyAmount = playerMaxSize;
								 }
								 player.closeInventory();
								 if (item.get(tiklamaCalculator).getSeller().equalsIgnoreCase(player.getName())) player.sendMessage(getLang.getText("Messages.cannotBuyOwnItem"));
								 else {
									 
									 ConfirmationStorage storage = new ConfirmationStorage(item.get(tiklamaCalculator), buyAmount, false);
									 ConfirmationGuiListener.confirmation.put(player.getName(), storage);
									 ConfirmationGui.createGui(player, storage, e.getCurrentItem().getItemMeta().getDisplayName());
										 
									 
								 }
								 
							 }
							 else if (e.getSlot() == 48 && e.getCurrentItem().getType() == Material.getMaterial(getLang.getText("Gui.previousPage.material"))) 
								 ItemGui.createGui(player, product, category, Integer.valueOf(page)-1);
							 else if (e.getSlot() == 50 && e.getCurrentItem().getType() == Material.getMaterial(getLang.getText("Gui.nextPage.material")))  
								 ItemGui.createGui(player, product, category, Integer.valueOf(page)+1);} 
					 }
			}
		}
	}

}
