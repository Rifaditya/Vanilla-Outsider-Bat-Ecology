package net.vanillaoutsider.bat_ecology;

import net.dasik.social.api.group.GroupMember;
import net.dasik.social.api.SocialEntity;
import net.minecraft.world.entity.ambient.Bat;

public interface BatExtensions extends SocialEntity, GroupMember<Bat>, net.dasik.social.api.breeding.UniversalAgeable {
    // dasik$getScheduler is inherited from SocialEntity
    // dasik$getSpeciesId, UniversalAgeable methods are inherited
}
