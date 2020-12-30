# InventoryAPI

## Présentation

<p>➠  Développeur(s) : BakaAless</p>
<p>➠  État : Fini</p>


## Description

➠  API permettant de faciliter la création d'inventaires Minecraft.
Cette API a été développée par et fait partie du projet [Avatar-Returns](https://avatar-returns.fr).

➠  Créer un inventaire et le faire ouvrir par un joueur (remplacez JavaPlugin par votre classe principale):
```java
final InventoryAPI inventory = InventoryAPI.create(JavaPlugin);
inventory.setSize(9);
inventory.setTitle("Mon premier inventaire");
inventory.build(player);
```

➠  Mettre un item dans l'inventaire sur un slot S, annuler le clic et envoyer un message au joueur :
```java
final InventoryAPI inventory = InventoryAPI.create(JavaPlugin);
inventory.setSize(9);
inventory.setTitle("Mon premier inventaire");
inventory.addItem(S, itemstack, true, inventoryClickEvent -> {
  player.sendMessage("Clic détecté");
});
inventory.build(player);
```

➠  Générer un item à partir d'une fonction et refresh l'inventaire tous les deux ticks :
```java
final InventoryAPI inventory = InventoryAPI.create(JavaPlugin);
inventory.setSize(9);
inventory.setTitle("Mon premier inventaire");
inventory.setRefresh(true);
inventory.addItem(S, o -> {
  final ItemStack itemStack = new ItemStack(Material.DIAMOND);
  final ItemMeta itemMeta = itemStack.getItemMeta();
  itemMeta.setDisplayName("§" + (new Random().nextInt(10)) + System.currentTimeMillis());
  itemStack.setItemMeta(itemMeta);
  return itemStack;
}, true, inventoryClickEvent -> {
  player.sendMessage("Clic détecté");
});
inventory.build(player);
```


## Licence

<p xmlns:dct="http://purl.org/dc/terms/" xmlns:cc="http://creativecommons.org/ns#" class="license-text"><a rel="cc:attributionURL" property="dct:title" href="https://github.com/BakaAless/InventoryAPI">InventoryAPI</a> by <a rel="cc:attributionURL dct:creator" property="cc:attributionName" href="https://github.com/BakaAless">BakaAless</a> is licensed under <a rel="license" href="https://creativecommons.org/licenses/by-nc-sa/4.0">CC BY-NC-SA 4.0<img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/cc.svg?ref=chooser-v1" /><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/by.svg?ref=chooser-v1" /><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/nc.svg?ref=chooser-v1" /><img style="height:22px!important;margin-left:3px;vertical-align:text-bottom;" src="https://mirrors.creativecommons.org/presskit/icons/sa.svg?ref=chooser-v1" /></a></p>
