package net.lars.spacemod.shader;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.util.Identifier;


public class FlameShaderManager {
    private static final Identifier ENGINE_FLAME_POST_PIPELINE = new Identifier("spacemod:engine_flame_post");
    private static final Identifier ENGINE_FLAME_POST_SHADER   = new Identifier("spacemod:engine_flame_shader"); // engine_flame_post

    public static float u_diamondMult = 2.0f;
    public static float u_flameMult = 1.0f;
    public static float u_overallMult = 3.3f;
    public static float u_repeat = 0.2f;
    public static float u_offset = 0.7f;

    public static float[] u_rocket_flame_buffer = new float[512];

    public static void registerShader(){

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((pipelineName, pipeline, context) -> {

            if (ENGINE_FLAME_POST_PIPELINE.equals(pipelineName)) {

                ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(ENGINE_FLAME_POST_SHADER);

                // Rotation angle in degrees that updates over time
                float sig = (System.currentTimeMillis() % 36000L) * 0.01f; // 0 to 360

                assert shader != null;
                shader.setFloat("u_sig", sig);

                shader.setFloat("u_diamondMult", u_diamondMult);
                shader.setFloat("u_flameMult", u_flameMult);
                shader.setFloat("u_overallMult", u_overallMult);
                shader.setFloat("u_repeat", u_repeat);
                shader.setFloat("u_offset", u_offset);

                for (int i = 0; i < 512; i++) {
                    float value = u_rocket_flame_buffer[i];
                    shader.setFloat("u_rocket_flame_buffer["+i+"]",value);
                }
            }

        });

        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(veilRenderer -> {
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            postProcessingManager.add(ENGINE_FLAME_POST_PIPELINE);
        });

    }

    public static void cleanUp(){
        for (int i = 0; i < 512; i++) {
            u_rocket_flame_buffer[i] = 0;
        }
    }
}
