package net.lars.spacemod.shader;

import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.lars.spacemod.Spacemod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public class ShaderManager {
    private static final Identifier ENGINE_FLAME_POST_PIPELINE = new Identifier("spacemod:engine_flame_post");
    private static final Identifier ENGINE_FLAME_POST_SHADER   = new Identifier("spacemod:engine_flame_shader"); // engine_flame_post

    public static void registerShader(){


        VeilEventPlatform.INSTANCE.preVeilPostProcessing((pipelineName, pipeline, context) -> {

            if ( ENGINE_FLAME_POST_PIPELINE.equals(pipelineName) ) {

                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                assert player != null;
                float pitch = (float)Math.toRadians( (double)player.getPitch() );
                float yaw   = (float)Math.toRadians( (double)player.getYaw() );

                pipeline.setFloat("u_pitch",(float)pitch);
                pipeline.setFloat("u_yaw",(float)yaw);
            }

        });

    }
}
