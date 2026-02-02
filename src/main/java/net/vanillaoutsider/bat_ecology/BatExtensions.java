package net.vanillaoutsider.bat_ecology;

import net.dasik.social.core.EntitySocialScheduler;
import net.dasik.social.api.SocialEntity;

public interface BatExtensions extends SocialEntity {
    // dasik$getScheduler is inherited from SocialEntity

    // dasik$getSpeciesId inherited
    void bat_ecology$setSocialSpecies(String species);

    void bat_ecology$setInLove(int ticks);

    boolean bat_ecology$isInLove();

    void bat_ecology$setBaby(boolean isBaby);

    boolean bat_ecology$isBaby();

    void bat_ecology$setBreedCooldown(int ticks);
}
