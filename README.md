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

## Documentation

➠ La documentation Java est disponible [ici](https://javadoc.jitpack.io/com/github/BakaAless/InventoryAPI/latest/javadoc/).

## Intégration

[![Release](https://jitpack.io/v/BakaAless/InventoryAPI.svg)](https://jitpack.io/#BakaAless/InventoryAPI)

➠  Pour intégrer ce code à gradle, en remplaçant `Version` par la version ci-dessus :
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation group: 'com.github.BakaAless', name: 'InventoryAPI', version: 'VERSION'
}
```

## Licence

InventoryAPI is under GPL-3.0 License.
