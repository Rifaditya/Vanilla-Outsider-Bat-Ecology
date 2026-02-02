<div align="center">

![Bat Ecology Banner](https://files.catbox.moe/placeholder_bat_ecology.png)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Java">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🦇 Bat Ecology

**No Backports:** I will **NOT** backport this mod to older versions (1.21, 1.20, etc.). Please do not ask.

In vanilla Minecraft, bats are "ambient particles with health." They squeak, fly into lava, and die.

**Vanilla Outsider: Bat Ecology** changes this foundation. Bats become a living, swarming part of the ecosystem. They have a hierarchy, meaningful behaviors, and a symbiotic relationship with your crops.

---

## ✨ Features

### 📡 The Hive Mind (Swarm Logic)

Bats now form cohesive **Swarms**.

- **Leader Election**: A unified AI system elects a "Leader" optimization to save performance.
- **Visual Scaling**: Larger colonies have physically larger bats, creating a visual dominance in caves.

### 🕯️ Lantern Interactions (Pollination)

Bats are attracted to Light at night.

- **Orbiting**: Leaders will seek out Lanterns and orbit them tight.
- **Crop Dusting**: While orbiting, the swarm releases "pollen/dust" that acts as **Bonemeal** for crops in a 9x9 area.
- **Strategy**: Build "Bat Highways" of lanterns to automate your farm overnight.

### 💩 Guano (Fertilizer)

Bats occasionally drop **Guano**.

- **Ambient**: A subtle way to get free fertilizer just by having them around.
- **Balance**: Configurable rates ensure it's a bonus, not a mess.

> [!NOTE]
> **Performance**: We use the **Dasik Library** "Highlander" system. Only 1 bat per swarm runs complex logic. The rest use simple Boid Flocking. You can have 200 bats with minimal tick impact.

### 🩸 Breeding

- **Item**: **Spider Eye**.
- **Method**: Feed two bats to start a colony.
- **Growth**: Babies take 20 minutes to mature, scaling with the colony size.

---

## ⚙️ Config

The mod works out of the box with zero setup.

- **Global Template**: `config/bat_ecology.json`
- **In-Game**: Use `/gamerule bd_bat_` to configure per-world settings.
  - `bd_bat_pollinate_chance`: Bonemeal success rate (Default: 100)
  - `bd_bat_swarm_max`: Natural spawn cap per group (Default: 10)
  - `bd_bat_guano_rate`: Drop frequency (Default: 2)
  - `bd_bat_roost_range`: Patrol radius (Default: 64)

---

## 📦 Install

1. Install **[Fabric API](https://modrinth.com/mod/fabric-api)**.
2. Install **[Dasik Library](https://modrinth.com/mod/dasik-library)**.
3. Download `Bat-Ecology.jar` and place it in your `mods` folder.

---

## 🧩 Compatibility

| Feature | Fabric (26.1+) |
| :--- | :---: |
| Singleplayer | ✅ |
| Multiplayer | ✅ |
| **VO: Better Dogs** | ✅ (Compatible ecosystems) |
| Dasik Library | ✅ (Required) |

---

## ☕ Support

If you enjoy **Bat Ecology** and the **Vanilla Outsider** philosophy, consider fueling the next update with a coffee!

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)

> [!NOTE]
> **Indonesian Users:** SocioBuzz supports local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Credits

| Role | Author |
| :--- | :--- |
| **Creator** | DasikIgaijin |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

---

> [!IMPORTANT]
> **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. Modrinth).
>
> **Cross-platform distribution is not permitted.** If you download this mod from Modrinth, your modpack must also be published on Modrinth.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
