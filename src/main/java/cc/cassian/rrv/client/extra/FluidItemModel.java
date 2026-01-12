package cc.cassian.rrv.client.extra;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Unit;

/**
 * A universal model for all fluid-items
 */
public class FluidItemModel extends Model {


    public FluidItemModel(ModelPart modelPart) {
        //? if >1.21.10 {
        /*super(modelPart, net.minecraft.client.renderer.rendertype.RenderTypes::entityTranslucent);
        *///?} else {
        super(modelPart, RenderType::entityTranslucent);
        //?}
    }

    public static LayerDefinition createFluidLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 0.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }
}
