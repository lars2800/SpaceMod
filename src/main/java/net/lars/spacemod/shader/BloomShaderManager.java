package net.lars.spacemod.shader;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.util.Identifier;

public class BloomShaderManager {
    //blurOffset
    private static final Identifier BLOOM_POST_PIPELINE = new Identifier("spacemod:bloom_post");
    private static final Identifier BLOOM_POST_SHADER   = new Identifier("spacemod:bloom_shader");

    public static float blurOffset = 5.0f;

    public static void registerShader(){

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((pipelineName, pipeline, context) -> {

            if (BLOOM_POST_PIPELINE.equals(pipelineName)) {

                ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(BLOOM_POST_SHADER);

                shader.setFloat("u_blurOffset", blurOffset);
            }

        });

        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(veilRenderer -> {
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            postProcessingManager.add(BLOOM_POST_PIPELINE);
        });

    }
}
