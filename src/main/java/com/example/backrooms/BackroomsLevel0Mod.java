package com.example.backrooms;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class BackroomsLevel0Mod implements ModInitializer {
    public static final String MOD_ID = "backrooms_level0";
    public static final RegistryKey<World> LEVEL_0 = RegistryKey.of(RegistryKeys.WORLD, id("level_0"));

    public static final Block YELLOW_WALLPAPER = registerBlock("yellow_wallpaper", new Block(FabricBlockSettings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).strength(1.2F).sounds(BlockSoundGroup.WOOL)));
    public static final Block DAMP_CARPET = registerBlock("damp_carpet", new Block(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).strength(0.8F).sounds(BlockSoundGroup.WOOL)));
    public static final Block CEILING_TILE = registerBlock("ceiling_tile", new Block(FabricBlockSettings.create().mapColor(MapColor.OAK_TAN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.0F).sounds(BlockSoundGroup.CALCITE)));
    public static final Block FLUORESCENT_LIGHT = registerBlock("fluorescent_light", new Block(FabricBlockSettings.create().mapColor(MapColor.WHITE).luminance(s -> 15).strength(0.4F).sounds(BlockSoundGroup.GLASS)));

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerManager().getPlayerList().forEach(this::tryTeleportFromSuffocation));
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (world.getRegistryKey().equals(LEVEL_0)) {
                buildLevel0Chunk(world, chunk.getPos());
            }
        });
    }

    private void tryTeleportFromSuffocation(ServerPlayerEntity player) {
        if (player.getWorld().getRegistryKey().equals(LEVEL_0) || player.age % 10 != 0) return;
        BlockState head = player.getWorld().getBlockState(BlockPos.ofFloored(player.getX(), player.getEyeY(), player.getZ()));
        if (!(head.getBlock() instanceof FallingBlock) || head.isOf(Blocks.WATER) || head.isOf(Blocks.LAVA)) return;
        ServerWorld target = player.getServer().getWorld(LEVEL_0);
        if (target == null) return;
        BlockPos spawn = findSafeSpawn(target, BlockPos.ofFloored(player.getX(), 65, player.getZ()));
        player.teleport(target, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYaw(), player.getPitch());
        player.sendMessage(Text.literal("Level 0\nThe Lobby"), true);
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleS2CPacket(Text.literal("Level 0")));
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SubtitleS2CPacket(Text.literal("The Lobby")));
        player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket(5, 20, 5));
    }

    private static BlockPos findSafeSpawn(ServerWorld world, BlockPos near) {
        ChunkPos chunkPos = new ChunkPos(near);
        buildLevel0Chunk(world, chunkPos);
        return new BlockPos(near.getX(), 65, near.getZ());
    }

    public static void buildLevel0Chunk(ServerWorld world, ChunkPos chunkPos) {
        int minX = chunkPos.getStartX();
        int minZ = chunkPos.getStartZ();
        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                world.setBlockState(new BlockPos(x, 63, z), DAMP_CARPET.getDefaultState(), Block.NOTIFY_LISTENERS);
                world.setBlockState(new BlockPos(x, 67, z), CEILING_TILE.getDefaultState(), Block.NOTIFY_LISTENERS);
                boolean wall = x % 9 == 0 || z % 11 == 0;
                boolean doorway = (Math.floorMod(x, 9) == 0 && Math.floorMod(z, 11) >= 4 && Math.floorMod(z, 11) <= 6)
                    || (Math.floorMod(z, 11) == 0 && Math.floorMod(x, 9) >= 3 && Math.floorMod(x, 9) <= 5);
                for (int y = 64; y <= 66; y++) {
                    world.setBlockState(new BlockPos(x, y, z), wall && !doorway ? YELLOW_WALLPAPER.getDefaultState() : Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
                if ((Math.floorMod(x, 8) == 4) && (Math.floorMod(z, 8) >= 3 && Math.floorMod(z, 8) <= 5)) {
                    world.setBlockState(new BlockPos(x, 67, z), FLUORESCENT_LIGHT.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.ITEM, id(name), new net.minecraft.item.BlockItem(block, new net.minecraft.item.Item.Settings()));
        return Registry.register(Registries.BLOCK, id(name), block);
    }

    public static Identifier id(String path) { return new Identifier(MOD_ID, path); }
}
