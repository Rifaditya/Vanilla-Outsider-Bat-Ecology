package com.vanillaoutsider.batecology.access;

import com.vanillaoutsider.batecology.ai.BatSwarmAI;
import org.jetbrains.annotations.ApiStatus;

/**
 * Duck interface for internal mixin access. Do not implement manually.
 * 
 * @deprecated Internal use only.
 */
@ApiStatus.Internal
@Deprecated
public interface BatEcologyEntity {
    BatSwarmAI getSwarmAI();

    int getColonyID();

    void setColonyID(int id);
}
