package moe.kouyou.momocommand

import moe.kouyou.momocommand.datastructs.*
import moe.kouyou.momocommand.utils.ListConstructor
import moe.kouyou.momocommand.utils.getCommandMap
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.Yaml
import java.io.File

object ConfigManager {
  class Config {
    lateinit var liveReload: Boolean
    lateinit var messages: MutableMap<String, String>
  }

  lateinit var config: Config

  fun messages(key: String): String = config.messages[key]!!

  fun loadConfig() {
    val f = File(pluginInst.dataFolder, "config.yml")
    if(!f.exists()) pluginInst.saveResource("config.yml", true)
    val br = f.bufferedReader()
    val cfg = br.readText()
    br.close()
    config = Yaml(Constructor(Config::class.java)).load(cfg) as Config
  }
}

object MechanicManager {
  val mechanics = hashMapOf<String, Mechanic>()

  fun getMechanic(name: String) = mechanics[name]

  fun loadMechanics() {
    val fol = File(pluginInst.dataFolder, "mechanics")
    if(!fol.exists()) {
      fol.mkdirs()
      pluginInst.saveResource("mechanics/example.yml", true)
    }
    val yaml = Yaml(ListConstructor(MechanicData::class.java))
    fol.listFiles()!!.map {
      val br = it.bufferedReader(Charsets.UTF_8)
      yaml.load<List<MechanicData>>(br.readText()).also { br.close() }
    }.flatten().map(MechanicData::toMechanic).forEach { mechanics[it.name] = it }
  }
}

object MCommandManager {
  val mcmds = hashMapOf<String, MCommand>()

  fun getMCommand(name: String) = mcmds[name]

  fun loadCommands() {
    val fol = File(pluginInst.dataFolder, "commands")
    if(!fol.exists()) {
      fol.mkdirs()
      pluginInst.saveResource("commands/example.yml", true)
    }
    val yaml = Yaml(ListConstructor(MCommandData::class.java))
    fol.listFiles()!!.map {
      val br = it.bufferedReader(Charsets.UTF_8)
      yaml.load<List<MCommandData>>(br.readText()).also { br.close() }
    }.flatten().map(MCommandData::toMCommand).forEach {
      mcmds[it.cmdLabel] = it
      it.register()
    }
  }

  fun unloadCommands() {
    (getCommandMap() as SimpleCommandMap).commands.removeIf { it is MCommand }
  }
}
