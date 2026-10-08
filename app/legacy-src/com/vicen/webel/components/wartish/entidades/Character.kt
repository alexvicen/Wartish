package com.vicen.webel.components.wartish.entidades

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.io.Serializable

@Entity
class Character() : Serializable {

    @Id
    var dbId: Long = 0

    var name: String = ""
    var level: Int = 0
    var experience: Int = 0
    var life: Int = 0
    var attack: Int = 0
    var magic: Int = 0
    var defense: Int = 0
    var speed: Int = 0
    var critic: Int = 0

    var helmetLevel: Int = 0
    var archLevel: Int = 0
    var shieldLevel: Int = 0
    var glovesLevel: Int = 0
    var bootsLevel: Int = 0
    var arrowLevel: Int = 0

    var nugget: Int = 0
    var iron: Int = 0
    var uncutGem: Int = 0
    var rock: Int = 0
    var log: Int = 0

    var goldIngot: Int = 0
    var ironIngot: Int = 0
    var gem: Int = 0
    var stone: Int = 0
    var woodenBoard: Int = 0

    constructor(name: String, level: Int) : this() {
        this.name = name
        this.level = level
        setLife()
        setAttack()
        setMagic()
        setDefense()
        setSpeed()
        setCritic()
    }

    private fun setLife() {
        life = (level * 70) + (helmetLevel * 5)
    }

    private fun setAttack() {
        attack = (level * 3) + (archLevel * 2) + arrowLevel
    }

    private fun setMagic() {
        attack = (level * 3) + (glovesLevel * 2)
    }

    private fun setDefense() {
        attack = (level * 3) + (shieldLevel * 2)
    }

    private fun setSpeed() {
        attack = (level * 3) + (bootsLevel * 2) + glovesLevel
    }

    private fun setCritic() {
        attack = (level * 3) + archLevel + glovesLevel + arrowLevel
    }

}