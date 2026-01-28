<div align="center">

![banner AI generated](https://i.imgur.com/placeholder.png)

</div>
<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&logo=fabric" alt="Requires Fabric API"></a>
    <img src="https://img.shields.io/badge/Loader-Fabric-orange?style=for-the-badge" alt="Fabric Support">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
</p>

# 🦇 Make Bats Worthy of the Ecosystem (Fabric Edition)

> [!CAUTION]
> **No Backports:** I will **NOT** backport this mod to older versions. Please do not ask.

Every Minecraft player knows the pain: bats are just useless lag-particles that squeak and fly into lava.

**Vanilla Outsider: Bat Ecology** completely overhauls bat AI to make them smarter, social, and essential for sustainable farming. Now powered by a high-performance **"1KB Haiku" Scheduler**, bats form complex colonies without lagging your world.

---

## ✨ Key Features

### 🌙 Dynamic Roosting (Circadian Rhythm)

Bats now strictly adhere to the day/night cycle, saving server performance and adding immersion.

| State | Condition | Behavior |
| :---: | :--- | :--- |
| 💤 | **Roosting (Day)** | Bats seek dark ceilings and hang upside down. **AI is virtually disabled (0 tick impact)**. |
| 🏰 | **Swarming (Night)** | At dusk (18:00), the colony wakes and follows a Leader to hunt. |
| 🦇 | **Panic** | If a player enters a quiet roosting cave, the colony scatters in panic. |

### 🧠 Swarm Intelligence (The "Boid" System)

Bats are no longer lone wanderers. They form tight-knit colonies with a functional hierarchy:

* **Leader System:** Every colony elects a Leader. Followers simply boid-flock to the Leader, drastically reducing pathfinding overhead.
* **Echolocation:** Isolated bats emit pitch-shifted squeaks to locate their colony.
* **Home Range:** A colony remembers its daytime roost and will not stray further than 64 blocks.

### 🌱 Farming Utility

Bats are finally useful.

* **Guano (Passive):** While flying, bats occasionally drop **Guano**, a weak fertilizer that accelerates plant growth.
* **Pollination (Active):** You can harness the swarm.
    1. Place a **Lantern** near your crops.
    2. At night, the swarm will navigate to the light.
    3. As they orbit, they release a **Pollination Burst**, bonemealing a **9x9 area**.

---

## ⚙️ Configuration (Native Game Rules)

Bat Ecology uses the **Native Minecraft Game Rules** system, ensuring 100% compatibility with snapshots and vanilla menus.

> [!IMPORTANT]
> **Bat Ecology Custom Category:**
> All settings are grouped into the **"Bat Ecology"** folder in the "Edit Game Rules" screen.

### 🛠 What can you configure?

* **Colony Size:** Adjust pack spawning limits (4-9 bats default).
* **Guano Rates:** Fine-tune how often bats fertilize the ground.
* **Pollination Power:** Adjust the effectiveness of the lantern ritual.

---

## 📦 Installation

1. Download the latest **Minecraft 26.1 Snapshot**.
2. Install **[Fabric Loader](https://fabricmc.net/)**.
3. Install **[Fabric API](https://modrinth.com/mod/fabric-api)**.
4. Place the `Vanilla-Outsider-Bat-Ecology.jar` in your `mods` folder.

---

## ☕ Support the Development

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/dasikigaijin/tip)
[![SocioBuzz](https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge)](https://sociabuzz.com/dasikigaijin/tribe)

> [!NOTE]
> **Indonesian Users:** SocioBuzz supports local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!

---

## 📜 Legal & Credits

| Role | Author |
| :--- | :--- |
| **Creator** | DasikIgaijin |
| **Collection** | Vanilla Outsider |
| **License** | GNU GPLv3 |

---

> [!IMPORTANT]
> This mod is part of the **Vanilla Outsider** collection. You are free to use it in modpacks, videos, and servers.
>
> > [!IMPORTANT]
> > **Modpack Permissions:** You are free to include this mod in modpacks, **provided the modpack is hosted on the same platform** (e.g. Modrinth).
> >
> > **Cross-platform distribution is not permitted.** If you download this mod from Modrinth, your modpack must also be published on Modrinth.

---

<div align="center">

**Made with ❤️ for the Minecraft community**

*Part of the Vanilla Outsider Collection*

</div>
