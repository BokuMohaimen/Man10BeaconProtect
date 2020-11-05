package man10bp.man10beaconprotect1

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Beacon
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Man10BeaconProtect1 : JavaPlugin() , Listener{

    val prefix = "[§eMan10§bBP§r]"

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(this,this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    fun setBeaconEvent(e: BlockPlaceEvent){

        val block = e.block

        if (block.type != Material.BEACON)return

        val beaconstate = block.state

        if (beaconstate !is Beacon)return

        this.addPermission(e.player,beaconstate)

        e.player.sendMessage( prefix + "ビーコンを保護しました")
    }
    @EventHandler
    fun clickBeaconEvent(e: PlayerInteractEvent) {

        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val block = e.clickedBlock ?: return

        if (block.type != Material.BEACON) return

        val beaconstate = block.state

        if (beaconstate !is Beacon) return

        val p = e.player

        if (!this.hasPermission(p,beaconstate)) {
            p.sendMessage(prefix + "§cあなたはこのビーコンを開く権限がありません！")
            e.isCancelled = true
            return
        }
    }
    @EventHandler
    fun breakBeaconEvent(e: BlockBreakEvent) {

        if (e.player.hasPermission("man10beaconprotect.op")) return

        val block = e.block

        if (block.type != Material.BEACON)return

        val state = block.state

        if (state !is Beacon)return

        val p = e.player

        if (!this.hasPermission(p,state)){
            p.sendMessage(prefix + "§cあなたはこのビーコンを壊す権限がありません")
            e.isCancelled = true
            return
        }
    }

    fun hasPermission(p: Player, beacon: Beacon):Boolean{

        val owners = beacon.persistentDataContainer[NamespacedKey(this,"owners"), PersistentDataType.STRING]?.split(";")?:return false

        if (owners.contains(p.uniqueId.toString()))return true

        return false

    }

    fun addPermission(p: Player, beacon: Beacon){

        var owners = beacon.persistentDataContainer[NamespacedKey(this,"owners"), PersistentDataType.STRING]?:""
        owners += "${p.uniqueId};"

        beacon.persistentDataContainer.set(NamespacedKey(this,"owners"), PersistentDataType.STRING,owners)

        beacon.update()

    }

}