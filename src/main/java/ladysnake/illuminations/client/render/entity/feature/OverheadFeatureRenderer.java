package ladysnake.illuminations.client.render.entity.feature;

import ladysnake.illuminations.client.IlluminationsClient;
import ladysnake.illuminations.client.render.CrownRenderLayer;
import ladysnake.illuminations.client.render.entity.model.OverheadModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.stream.Collectors;

public class OverheadFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final Map<String, ResolvedOverheadData> models;

    public OverheadFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext, EntityRendererFactory.Context loader) {
        super(featureRendererContext);
        this.models = IlluminationsClient.OVERHEADS_DATA.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, data -> new ResolvedOverheadData(data.getValue().getTexture(), data.getValue().createModel(loader))));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (IlluminationsClient.PLAYER_COSMETICS.get(entity.getUuid()) != null && !entity.isInvisible()) {
            String playerOverhead = IlluminationsClient.PLAYER_COSMETICS.get(entity.getUuid()).getOverhead();
            if (playerOverhead != null) {
                ResolvedOverheadData resolvedOverheadData = this.models.get(playerOverhead);
                if (resolvedOverheadData != null) {
                    Identifier texture = resolvedOverheadData.texture();
                    OverheadModel model = resolvedOverheadData.model();

                    model.head.pivotX = this.getContextModel().head.pivotX;
                    model.head.pivotY = this.getContextModel().head.pivotY;
                    model.head.pitch = this.getContextModel().head.pitch;
                    model.head.yaw = this.getContextModel().head.yaw;
                    model.render(matrices, vertexConsumers.getBuffer(CrownRenderLayer.getCrown(texture)), 15728880, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
                }
            }
        }
    }

    private record ResolvedOverheadData(Identifier texture, OverheadModel model) { }
}
