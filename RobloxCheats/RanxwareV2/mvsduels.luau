-- Ranxware V2: REVIVED | MVSD
-- (rewrite cuz the old one sucks)

local mvsd = {
    remotes = {
        Shoot = game.ReplicatedStorage.Remotes.Shoot,
        ThrowStart = game.ReplicatedStorage.Remotes.ThrowStart,
        ThrowHit = game.ReplicatedStorage.Remotes.ThrowHit,
        Stab = game.ReplicatedStorage.Remotes.Stab,
        OnMatchFinished = game.ReplicatedStorage.Remotes.OnMatchFinished,
        OnRoundEnded = game.ReplicatedStorage.Remotes.OnRoundEnded,
        OnPlayerKilled = game.ReplicatedStorage.Remotes.OnPlayerKilled,
        OnRoleSelection = game.ReplicatedStorage.Remotes.OnRoleSelection
    },
    you = {
        lplr = game.Players.LocalPlayer,
        character = game.Players.LocalPlayer.Character,
        humanoid = game.Players.LocalPlayer.Character and game.Players.LocalPlayer.Character:FindFirstChildOfClass("Humanoid"),
        status = {
            matchId = 0,
            inMatch = false,
            gamemode = "Classic"
        }
    }
}

local funcs = {
    getAngle = function(targetpos, localpos)
        local normal = Vector3.new()
        if targetpos and localpos then
            normal = (targetpos - localpos).Unit
        end
        return normal
    end,
    getMouseLocation = function()
        return game:GetService("UserInputService"):GetMouseLocation()
    end,
    sayMessage = function(message)
        pcall(function()
            game:GetService("TextChatService").ChatInputBarConfiguration.TargetTextChannel:SendAsync(message)
        end)
    end,
    getEnemies = function()
        local enemies = {}
        if mvsd.you.status.inMatch then
            for _,plr in next,game.Players:GetPlayers() do
                if plr ~= mvsd.you.lplr and plr:GetAttribute("Match") and plr:GetAttribute("Match") == mvsd.you.status.matchId and plr.Team ~= mvsd.you.lplr.Team then
                    table.insert(enemies, plr)
                end
            end
        end
        return enemies
    end,
    getAllies = function()
        local teammates = {}
        if mvsd.you.status.inMatch then
            for _,plr in next,game.Players:GetPlayers() do
                if plr ~= mvsd.you.lplr and plr:GetAttribute("Match") and plr:GetAttribute("Match") == mvsd.you.status.matchId and plr.Team == mvsd.you.lplr.team then
                    table.insert(teammates, plr)
                end
            end
        end
        return teammates
    end,
    getTeamStatus = function(player)
        if player ~= mvsd.you.lplr and player:GetAttribute("Match") and player:GetAttribute("Match") == mvsd.you.status.matchId then
            if player.Team ~= mvsd.you.lplr.Team then
                return "Enemy"
            else
                return "Ally"
            end
        end
    end,
    getGamemode = function()
        return mvsd.you.lplr:GetAttribute("Gamemode")
    end,
    isAlive = function(character)
        return character and character:FindFirstChildOfClass("Humanoid") and character:FindFirstChildOfClass("Humanoid").Health > 0
    end,
    getPlayerNearestCursor = function(size)
        local closest = size
        local target = nil
        for _,player in next,game.Players:GetPlayers() do
            if player ~= mvsd.you.lplr and player:GetAttribute("Match") == mvsd.you.status.matchId then
				if player.Team ~= mvsd.you.lplr.Team then
					if player.Character and player.Character:FindFirstChildOfClass("Humanoid") and player.Character:FindFirstChildOfClass("Humanoid").Health > 0 then
						local vector = workspace.CurrentCamera:WorldToScreenPoint(player.Character.HumanoidRootPart.Position)
						if vector then
							local distance = (game:GetService("UserInputService"):GetMouseLocation() - Vector2.new(vector.X,vector.Y)).Magnitude
							if distance <= closest then
								closest = distance
								target = player
							end
						end
					end
				end
            end
        end
        return target
    end,
	getPlayersNearPosition = function(size)
		local targets = {}
		for _,player in next,game.Players:GetPlayers() do
			if player ~= mvsd.you.lplr and player:GetAttribute("Match") == mvsd.you.status.matchId then
				if player.Team ~= mvsd.you.lplr.Team then
					local char = player.Character
					if char and char:FindFirstChildOfClass("Humanoid") and char:FindFirstChildOfClass("Humanoid").Health > 0 then
						if mvsd.you.character and mvsd.you.character:FindFirstChildOfClass("Humanoid") and mvsd.you.character:FindFirstChildOfClass("Humanoid").Health > 0 then
							local root = char.HumanoidRootPart
							local distance = (root.Position - mvsd.you.character.HumanoidRootPart.Position).Magnitude
							if distance <= size then
								table.insert(targets, player)
							end
						end
					end
				end
            end
		end
		return targets
	end,
	getKnife = function()
		local knife = nil
		for _,tool in next,mvsd.you.lplr.Backpack:GetChildren() do
			if tool:IsA("Tool") then
				if game:GetService("CollectionService"):HasTag(tool, "KnifeTool") then
					knife = tool
				end
			end
		end
		for _,tool in next,mvsd.you.character:GetChildren() do
			if tool:IsA("Tool") then
				if game:GetService("CollectionService"):HasTag(tool, "KnifeTool") then
					knife = tool
				end
			end
		end
		return knife
	end,
	getRevolver = function()
		local revolver = nil
		for _,tool in next,mvsd.you.lplr.Backpack:GetChildren() do
			if tool:IsA("Tool") then
				if game:GetService("CollectionService"):HasTag(tool, "GunTool") then
					revolver = tool
				end
			end
		end
		for _,tool in next,mvsd.you.character:GetChildren() do
			if tool:IsA("Tool") then
				if game:GetService("CollectionService"):HasTag(tool, "GunTool") then
					revolver = tool
				end
			end
		end
		return revolver
	end
}

local ranxConnections = {
    connections = {}
}

function ranxConnections:BindConnection(name,con)
    if not ranxConnections.connections[name] then
		ranxConnections.connections[name] = con
	else
		ranxConnections.connections[name]:Disconnect()
		ranxConnections.connections[name] = con
	end
end

function ranxConnections:BindToRenderStep(name,func)
	local con = game:GetService("RunService").RenderStepped:Connect(func)
	if not ranxConnections.connections[name] then
		ranxConnections.connections[name] = con
	else
		ranxConnections.connections[name]:Disconnect()
		ranxConnections.connections[name] = con
	end
end

function ranxConnections:BindToHeartbeat(name,func)
	local con = game:GetService("RunService").Heartbeat:Connect(func)
	if not ranxConnections.connections[name] then
		ranxConnections.connections[name] = con
	else
		ranxConnections.connections[name]:Disconnect()
		ranxConnections.connections[name] = con
	end
end

function ranxConnections:BindToStep(name,func)
	local con = game:GetService("RunService").Stepped:Connect(func)
	if not ranxConnections.connections[name] then
		ranxConnections.connections[name] = con
	else
		ranxConnections.connections[name]:Disconnect()
		ranxConnections.connections[name] = con
	end
end

function ranxConnections:UnbindConnection(name)
	if ranxConnections.connections[name] then
		ranxConnections.connections[name]:Disconnect()
	end
end

mvsd.you.status.matchId = mvsd.you.lplr:GetAttribute("Match") or 0
mvsd.you.status.inMatch = mvsd.you.status.matchId ~= 0
local currentlyRespawning = false
local roundEnded = false

ranxConnections:BindConnection("CharacterRespawn", mvsd.you.lplr.CharacterAdded:Connect(function(newchar)
	currentlyRespawning = true
	task.wait(0.3)
	mvsd.you.character = newchar
	mvsd.you.humanoid = mvsd.you.character:FindFirstChildOfClass("Humanoid") or newchar:FindFirstChildOfClass("Humanoid")
	mvsd.you.status.matchId = mvsd.you.lplr:GetAttribute("Match") or 0
	mvsd.you.status.inMatch = (mvsd.you.status.matchId ~= 0)
	roundEnded = false
	currentlyRespawning = false
end))
ranxConnections:BindConnection("NormalRoundEnded", mvsd.remotes.OnRoundEnded.OnClientEvent:Connect(function()
	roundEnded = true
end))

local lib = loadstring(game:HttpGet('https://sirius.menu/rayfield'))()
local window = lib:CreateWindow({
	Name = "Ranxware V2 [ALPHA] | MVSD",
	LoadingTitle = "Ranxware V2 REVIVED",
	LoadingSubtitle = "by: AsteriskCodes",
	ConfigurationSaving = {
		Enabled = false
	}
})

local combatTab = window:CreateTab("Combat")
local playerTab = window:CreateTab("Player")
local visualsTab = window:CreateTab("Visuals")
local miscTab = window:CreateTab("Misc")

-- COMBAT --
combatTab:CreateSection("- Hitbox Spoofs -")
task.spawn(function()
	local hitboxes = {CurrentValue = false}
	local hitboxSize = {CurrentValue = 2}
	local hitboxTransparency = {CurrentValue = 0.8}
	hitboxes = combatTab:CreateToggle({
		Name = "Spoof Hitboxes",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and mvsd.you.status.inMatch then
							for _,i in next,funcs.getEnemies() do
								local char = i.Character
								if funcs.isAlive(char) then
									local root = char.HumanoidRootPart
									local hitbox = root:FindFirstChild("Part")
									if root and hitbox then
										local realRootSize = math.clamp(hitboxSize.CurrentValue, 2, 5)
										root.Size = Vector3.new(2, 2, 0.92) * realRootSize
										hitbox.Size = Vector3.new(2, 2, 2) * hitboxSize.CurrentValue
										hitbox.Transparency = hitboxTransparency.CurrentValue
									end
								end
							end
						end
						task.wait()
					until not hitboxes.CurrentValue
				end)
			else
				for _,i in next,funcs.getEnemies() do
					local char = i.Character
					if funcs.isAlive(char) then
						local root = char.HumanoidRootPart
						local hitbox = root:FindFirstChild("Part")
						root.Size = Vector3.new(2, 2, 0.92)
						hitbox.Size = Vector3.new(2.4, 2.4, 1.38)
						hitbox.Transparency = 1
					end
				end
			end
		end
	})
	hitboxSize = combatTab:CreateSlider({
		Name = "Hitbox Size",
		Range = {2,12},
		CurrentValue = 2,
		Increment = 1,
		Callback = function() end
	})
	hitboxTransparency = combatTab:CreateSlider({
		Name = "Hitbox Transparency",
		Range = {0.1,1},
		CurrentValue = 0.8,
		Increment = 0.05,
		Callback = function() end
	})
end)

-- knife silent-aim is just instakill bc im too tired to make a legit delay system lel
combatTab:CreateSection("- Aimbot Configs -")
task.spawn(function()
	local silentAim = {CurrentValue = false}
	local silentAimFOVSize = {CurrentValue = 100}
	local silentAimFOVVisible = {CurrentValue = true}
	local silentAimFOVColor = {Color = Color3.new(1,0,0)}
	local silentAimAimline = {CurrentValue = false}
	local silentAimLegit = {CurrentValue = true}
	local silentAimWallbang = {CurrentValue = false}
	local silentAimParams = RaycastParams.new()
	silentAimParams.RespectCanCollide = true
	silentAimParams.FilterType = Enum.RaycastFilterType.Exclude
	local circle = Drawing.new("Circle")
	circle.Color = Color3.new(1,0,0)
	circle.Thickness = 1
	circle.NumSides = 1e9
	circle.Radius = 100
	circle.Filled = false
	circle.Visible = false
	local aimline = Drawing.new("Line")
	aimline.Color = Color3.new(1,0,0)
	aimline.Thickness = 1
	aimline.Visible = false
	local silentAimHook;silentAimHook = hookmetamethod(game, "__namecall", function(self, ...)
		local args = {...}
		if silentAim.CurrentValue and not checkcaller() and getnamecallmethod() == "FireServer" then
			if tostring(self) == "Shoot" then
				local target = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue)
				if target then
					local hitbox = target.Character.HumanoidRootPart
					if silentAimLegit.CurrentValue then args[2] = hitbox.CFrame.Position end
					silentAimParams.FilterDescendantsInstances = {mvsd.you.character, funcs.getAllies()}
					local result = workspace:Raycast(args[1], funcs.getAngle(hitbox.CFrame.Position, args[1]) * 9e9, silentAimParams)
					if result then
						args[3] = result.Instance
						if silentAimLegit.CurrentValue then args[4] = result.Position end
						if silentAimWallbang.CurrentValue then
							args[3] = hitbox.Part
							if silentAimLegit.CurrentValue then args[4] = hitbox.CFrame.Position end
						end
					else
						args[3] = hitbox.Part
						if silentAimLegit.CurrentValue then args[4] = hitbox.CFrame.Position end
					end
				end
			elseif tostring(self) == "ThrowStart" then
				local target = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue)
				if target then
					local hitbox = target.Character.HumanoidRootPart
					if silentAimLegit.CurrentValue then args[2] = hitbox.CFrame.Position end
					silentAimParams.FilterDescendantsInstances = {mvsd.you.character, funcs.getAllies()}
					local result = workspace:Raycast(args[1], funcs.getAngle(hitbox.CFrame.Position, args[1]) * 9e9, silentAimParams)
					local angle = funcs.getAngle(hitbox.CFrame.Position, args[1])
					if result then
						if silentAimLegit.CurrentValue then args[2] = funcs.getAngle(hitbox.CFrame.Position, result.Position) end
						if result.Instance.Parent == target.Character then
							mvsd.remotes.ThrowHit:FireServer(unpack({
								[1] = hitbox.Part,
								[2] = Vector3.new()
							}))
						end
						if silentAimWallbang.CurrentValue then
							if silentAimLegit.CurrentValue then args[2] = angle end
							mvsd.remotes.ThrowHit:FireServer(unpack({
								[1] = hitbox.Part,
								[2] = Vector3.new()
							}))
						end
					else
						if silentAimLegit.CurrentValue then args[2] = angle end
						mvsd.remotes.ThrowHit:FireServer(unpack({
							[1] = hitbox.Part,
							[2] = Vector3.new()
						}))
					end
				end
			end
			return self.FireServer(self, unpack(args))
		end
		return silentAimHook(self, ...)
	end)
	silentAim = combatTab:CreateToggle({
		Name = "Silent-Aim [BETA]",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToRenderStep("FovCircleRender", function()
					circle.Visible = silentAim.CurrentValue and silentAimFOVVisible.CurrentValue
					circle.Radius = silentAimFOVSize.CurrentValue
					circle.Position = funcs.getMouseLocation()
					circle.Color = silentAimFOVColor.Color
					aimline.Color = silentAimFOVColor.Color
					local target = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue)
					if target then
						local root = target.Character.HumanoidRootPart
						local viewpoint, vis = workspace.CurrentCamera:WorldToViewportPoint(root.Position)
						aimline.Visible = silentAim.CurrentValue and silentAimAimline.CurrentValue and vis
						aimline.From = funcs.getMouseLocation()
						aimline.To = Vector2.new(viewpoint.X, viewpoint.Y)
					else
						aimline.Visible = false
						aimline.From = Vector2.new()
						aimline.To = Vector2.new()
					end
				end)
			else
				ranxConnections:UnbindConnection("FovCircleRender")
				circle.Visible = false
				aimline.Visible = false
				aimline.From = Vector2.new()
				aimline.To = Vector2.new()
			end
		end
	})
	silentAimFOVSize = combatTab:CreateSlider({
		Name = "FOV Size",
		Range = {10,1000},
		Increment = 10,
		CurrentValue = 100,
		Callback = function() end
	})
	silentAimFOVVisible = combatTab:CreateToggle({
		Name = "Show FOV Circle",
		CurrentValue = true,
		Callback = function() end
	})
	silentAimFOVColor = combatTab:CreateColorPicker({
		Name = "FOV Color",
		Color = Color3.new(1,0,0),
		Callback = function() end
	})
	silentAimAimline = combatTab:CreateToggle({
		Name = "Show Aimline",
		CurrentValue = false,
		Callback = function() end
	})
	silentAimLegit = combatTab:CreateToggle({
		Name = "Legit Mode",
		CurrentValue = true,
		Callback = function() end
	})
	silentAimWallbang = combatTab:CreateToggle({
		Name = "Wallbang",
		CurrentValue = false,
		Callback = function() end
	})
end)


combatTab:CreateSection("- Weapon Spoofs -")
task.spawn(function()
	local spoofGunCooldown = {CurrentValue = false}
	local cooldownDuration = {CurrentValue = 2.5}
	spoofGunCooldown = combatTab:CreateToggle({
		Name = "Spoof Gun Cooldown",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and mvsd.you.status.inMatch then
							local revolver = funcs.getRevolver()
							if revolver then
								revolver:SetAttribute("Cooldown", cooldownDuration.CurrentValue)
							end
						end
						task.wait()
					until not spoofGunCooldown.CurrentValue
				end)
			else
				if mvsd.you.status.inMatch then
					local revolver = funcs.getRevolver()
					if revolver then
						revolver:SetAttribute("Cooldown", 2.5)
					end
				end
			end
		end
	})
	cooldownDuration = combatTab:CreateSlider({
		Name = "Cooldown Duration",
		Range = {0,2.5},
		CurrentValue = 2.5,
		Increment = 0.1,
		Callback = function()
			if spoofGunCooldown.CurrentValue then
				if mvsd.you.status.inMatch then
					local revolver = funcs.getRevolver()
					if revolver and revolver.Parent == mvsd.you.character then
						task.spawn(function()
							revolver.Parent = mvsd.you.lplr.Backpack
							task.wait(0.1)
							revolver.Parent = mvsd.you.character
						end)
					end
				end
			end
		end
	})
end)
task.spawn(function()
	local fastKnife = {CurrentValue = false}
	fastKnife = combatTab:CreateToggle({
		Name = "Fast Knife",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and mvsd.you.status.inMatch then
							local knife = funcs.getKnife()
							if knife then
								knife:SetAttribute("ThrowSpeed", 1000)
							end
						end
						task.wait()
					until not fastKnife.CurrentValue
				end)
			else
				if mvsd.you.status.inMatch then
					local knife = funcs.getKnife()
					if knife then
						knife:SetAttribute("ThrowSpeed", 125)
					end
				end
			end
		end
	})
end)
local propellerAnim = false
task.spawn(function()
	local customGunAnimation = {CurrentValue = false}
	customGunAnimation = combatTab:CreateToggle({
		Name = "Custom Gun Animation",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and mvsd.you.status.inMatch then
							local revolver = funcs.getRevolver()
							if revolver then
								if not propellerAnim then
									revolver:SetAttribute("EquipAnimation","Sniper_Equip")
								end
								revolver:SetAttribute("PulloutAnimation","Sniper_Pullout")
							end
						end
						task.wait()
					until not customGunAnimation.CurrentValue
				end)
			end
		end
	})
end)
task.spawn(function()
	local weaponSpin = {CurrentValue = false}
	weaponSpin = combatTab:CreateToggle({
		Name = "Weapon Spin",
		CurrentValue = false,
		Callback = function(value)
			propellerAnim = value
			if value then
				task.spawn(function()
					repeat
						if mvsd.you.status.inMatch then
							local knife = funcs.getKnife()
							if knife then
								knife:SetAttribute("EquipAnimation", "Propeller")
							end
							local revlover = funcs.getRevolver()
							if revlover then
								revlover:SetAttribute("EquipAnimation", "Propeller")
							end
						end
						task.wait()
					until not weaponSpin.CurrentValue
				end)
			else
				if mvsd.you.status.inMatch then
					local knife = funcs.getKnife()
					if knife then
						knife:SetAttribute("EquipAnimation", "Knife_Equip")
					end
					local revlover = funcs.getRevolver()
					if revlover then
						revlover:SetAttribute("EquipAnimation", "Gun_Equip")
					end
				end
			end
		end
	})
end)

combatTab:CreateSection("- Killaura Configs -")
local killallenabled = false
task.spawn(function()
	local rangepart = Instance.new("Part", workspace.CurrentCamera)
	rangepart.Transparency = 1
	rangepart.CanCollide = false
	rangepart.Anchored = true
	rangepart.Size = Vector3.new(1,1,1)
	local rangevis = Instance.new("CylinderHandleAdornment", game.CoreGui)
	rangevis.Color3 = Color3.new(1,0,0)
	rangevis.Height = 0
	rangevis.CFrame = CFrame.new() * CFrame.fromOrientation(math.rad(90), 0, 0)
	local killaura = {CurrentValue = false}
	local killauraRadius = {CurrentValue = 12}
	local killauraMaxAngle = {CurrentValue = 360}
	local killauraShowRange = {CurrentValue = false}
	killaura = combatTab:CreateToggle({
		Name = "Killaura",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("KillauraRangeRendering", function()
					local character = mvsd.you.character
					if not currentlyRespawning and funcs.isAlive(character) then
						local position = character.HumanoidRootPart.CFrame.Position
						rangepart.CFrame = CFrame.new(position.X, position.Y - 2.5, position.Z)
						rangevis.Adornee = rangepart
						rangevis.Visible = killaura.CurrentValue and killauraShowRange.CurrentValue
						rangevis.Radius = killauraRadius.CurrentValue
						rangevis.InnerRadius = killauraRadius.CurrentValue - 0.5
					end
				end)
				task.spawn(function()
					repeat
						if not currentlyRespawning then
							if mvsd.you.status.inMatch and funcs.isAlive(mvsd.you.character) and not killallenabled and not roundEnded then
								local knife = funcs.getKnife()
								local revolver = funcs.getRevolver()
								for _,i in next,funcs.getPlayersNearPosition(killauraRadius.CurrentValue) do
									if funcs.isAlive(i.Character) then
										local root = i.Character.HumanoidRootPart
										local direction = funcs.getAngle(root.Position, mvsd.you.character.HumanoidRootPart.Position)
										local dot = mvsd.you.character.HumanoidRootPart.CFrame.LookVector:Dot(direction)
										if math.acos(dot) >= (math.rad(killauraMaxAngle.CurrentValue / 2)) then continue end
										if knife and knife.Parent == mvsd.you.character then
											mvsd.remotes.Stab:FireServer(unpack({
												[1] = i.Character.Head
											}))
											task.wait()
										end
										if revolver and revolver.Parent == mvsd.you.character then
											mvsd.remotes.Shoot:FireServer(unpack({
												[1] = root.Position,
												[2] = root.Position,
												[3] = i.Character.Head.Part,
												[4] = root.Position
											}))
											task.wait()
										end
									end
								end
							end
						end
						task.wait()
					until not killaura.CurrentValue
				end)
			else
				ranxConnections:UnbindConnection("KillauraRangeRendering")
				rangevis.Adornee = nil
				rangevis.Visible = false
			end
		end
	})
	killauraRadius = combatTab:CreateSlider({
		Name = "Killaura Range",
		Range = {4,35},
		CurrentValue = 12,
		Increment = 1,
		Suffix = "studs",
		Callback = function() end
	})
	killauraMaxAngle = combatTab:CreateSlider({
		Name = "Killaura Max Angle",
		Range = {1,360},
		CurrentValue = 360,
		Increment = 1,
		Callback = function() end
	})
	killauraShowRange = combatTab:CreateToggle({
		Name = "Show Aura Range",
		CurrentValue = true,
		Callback = function() end
	})
end)

combatTab:CreateSection("- Kill-All Configs -")
task.spawn(function()
	local killAll = {CurrentValue = false}
	local killAllDelay = {CurrentValue = 0}
	killAll = combatTab:CreateToggle({
		Name = "Kill-All",
		CurrentValue = false,
		Callback = function(value)
			killallenabled = value
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning then
							if mvsd.you.status.inMatch and funcs.isAlive(mvsd.you.character) and not roundEnded then
								task.wait(0.5)
								local knife = funcs.getKnife()
								local revolver = funcs.getRevolver()
								if knife then
									if knife.Parent ~= mvsd.you.character then
										knife.Parent = mvsd.you.character
										task.wait(0.1)
									end
								else
									if revolver then
										if revolver.Parent ~= mvsd.you.character then
											revolver.Parent = mvsd.you.character
											task.wait(0.1)
										end
									end
								end
								for _,i in next,funcs.getEnemies() do
									local char = i.Character
									if funcs.isAlive(char) then
										local root = char.HumanoidRootPart
										if knife and knife.Parent == mvsd.you.character then
											mvsd.remotes.ThrowHit:FireServer(unpack({
												[1] = char.Head.Part,
												[2] = Vector3.new()
											}))
											task.wait()
										else
											if revolver and revolver.Parent == mvsd.you.character then
												mvsd.remotes.Shoot:FireServer(unpack({
													[1] = root.Position,
													[2] = root.Position,
													[3] = char.Head.Part,
													[4] = root.Position
												}))
												task.wait()
											end
										end
									end
									task.wait(killAllDelay.CurrentValue)
								end
							end
						end
						task.wait()
					until not killAll.CurrentValue
				end)
			end
		end
	})
	killAllDelay = combatTab:CreateSlider({
		Name = "Kill-All Delay",
		Range = {0,0.8},
		CurrentValue = 0,
		Increment = 0.1,
		Callback = function() end
	})
end)

-- PLAYER --
local movementLobbyCheck = true
local flyenabled = false
playerTab:CreateSection("- Humanoid Spoofs -")
task.spawn(function()
	local speedParams = RaycastParams.new()
	speedParams.RespectCanCollide = true
	speedParams.FilterType = Enum.RaycastFilterType.Exclude
	local walkspeedValue = {CurrentValue = 16}
	local walkspeedMode = {CurrentOption = {"Velocity"}}
	local walkspeedClimbDisable = {CurrentValue = true}
	playerTab:CreateToggle({
		Name = "Spoof Speed",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("SpeedSpoofer", function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
						if (walkspeedClimbDisable.CurrentValue and mvsd.you.humanoid:GetState() == Enum.HumanoidStateType.Climbing) or flyenabled or (movementLobbyCheck and not mvsd.you.status.inMatch) then return end
						speedParams.FilterDescendantsInstances = {mvsd.you.character}
						local movevec = (mvsd.you.humanoid.MoveDirection).Unit
						movevec = movevec == movevec and Vector3.new(movevec.X, 0, movevec.Z) or Vector3.new()
						local vel = movevec * walkspeedValue.CurrentValue
						if walkspeedMode.CurrentOption[1] ~= "Normal" then
							mvsd.you.humanoid.WalkSpeed = 16
						end
						if walkspeedMode.CurrentOption[1] == "Normal" then
							mvsd.you.humanoid.WalkSpeed = walkspeedValue.CurrentValue
						elseif walkspeedMode.CurrentOption[1] == "Velocity" then
							mvsd.you.character.HumanoidRootPart.Velocity = Vector3.new(vel.X, mvsd.you.character.HumanoidRootPart.Velocity.Y, vel.Z)
						end
					end
				end)
			else
				ranxConnections:UnbindConnection("SpeedSpoofer")
				if funcs.isAlive(mvsd.you.character) then
					mvsd.you.humanoid.WalkSpeed = 16
				end
			end
		end
	})
	walkspeedValue = playerTab:CreateSlider({
		Name = "Speed Value",
		Range = {16,100},
		CurrentValue = 16,
		Increment = 1,
		Callback = function() end
	})
	walkspeedMode = playerTab:CreateDropdown({
		Name = "Speed Method",
		Options = {"Normal","Velocity"},
		CurrentOption = {"Velocity"},
		Callback = function() end
	})
end)
task.spawn(function()
	local jumppowerValue = {CurrentValue = 50}
	playerTab:CreateToggle({
		Name = "Spoof Jump Power",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("JumppowerSpoofer", function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
						mvsd.you.humanoid.UseJumpPower = true
						mvsd.you.humanoid.JumpPower = jumppowerValue.CurrentValue
					end
				end)
			else
				ranxConnections:UnbindConnection("JumppowerSpoofer")
				if funcs.isAlive(mvsd.you.character) then
					mvsd.you.humanoid.JumpPower = 50
				end
			end
		end
	})
	jumppowerValue = playerTab:CreateSlider({
		Name = "Jump Power Value",
		Range = {50,200},
		CurrentValue = 50,
		Increment = 5,
		Callback = function() end
	})
end)

local infjumpenabled = false
playerTab:CreateSection("- Fly Configs -")
task.spawn(function()
	local flyingStates = {
		up = false,
		down = false
	}
	local fly = {CurrentValue = false}
	local flySpeed = {CurrentValue = 16}
	local flyVerticalSpeed = {CurrentValue = 50}
	local flyKeys = {CurrentOption = {"Space/LeftControl"}}
	fly = playerTab:CreateToggle({
		Name = "Fly",
		CurrentValue = false,
		Callback = function(value)
			flyenabled = value
			if value then
				ranxConnections:BindConnection("FlyKeysDownBind", game:GetService("UserInputService").InputBegan:Connect(function(input,gpe)
					if gpe then return end
					local division = string.split(flyKeys.CurrentOption[1], "/")
					if input.KeyCode == Enum.KeyCode[division[1]] then
						flyingStates.up = true
					elseif input.KeyCode == Enum.KeyCode[division[2]] then
						flyingStates.down = true
					end
				end))
				ranxConnections:BindConnection("FlyKeysUpBind", game:GetService("UserInputService").InputEnded:Connect(function(input,gpe)
					if gpe then return end
					local division = string.split(flyKeys.CurrentOption[1], "/")
					if input.KeyCode == Enum.KeyCode[division[1]] then
						flyingStates.up = false
					elseif input.KeyCode == Enum.KeyCode[division[2]] then
						flyingStates.down = false
					end
				end))
				ranxConnections:BindToHeartbeat("FlyLoop", function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
						if (movementLobbyCheck and not mvsd.you.status.inMatch) then return end
						mvsd.you.humanoid:ChangeState(Enum.HumanoidStateType.Freefall)
						local movevec = (mvsd.you.humanoid.MoveDirection).Unit
						movevec = movevec == movevec and Vector3.new(movevec.X, 0, movevec.Z) or Vector3.new()
						mvsd.you.character.HumanoidRootPart.Velocity = (movevec * flySpeed.CurrentValue) + Vector3.new(0, (flyingStates.up and flyVerticalSpeed.CurrentValue or 0) + (flyingStates.down and -flyVerticalSpeed.CurrentValue or 0), 0)
					end
				end)
			else
				ranxConnections:UnbindConnection("FlyKeysDownBind")
				ranxConnections:UnbindConnection("FlyKeysUpBind")
				ranxConnections:UnbindConnection("FlyLoop")
				flyingStates.up = false
				flyingStates.down = false
			end
		end
	})
	flySpeed = playerTab:CreateSlider({
		Name = "Fly Speed",
		Range = {16,100},
		Increment = 1,
		CurrentValue = 16,
		Callback = function() end
	})
	flyVerticalSpeed = playerTab:CreateSlider({
		Name = "Fly Vertical Speed",
		Range = {20,150},
		Increment = 2,
		CurrentValue = 50,
		Callback = function() end
	})
	flyKeys = playerTab:CreateDropdown({
		Name = "Fly Keys",
		Options = {"Space/LeftControl","Space/LeftShift","E/Q","Space/Q"},
		CurrentOption = {"Space/LeftControl"},
		Callback = function() end
	})
end)

playerTab:CreateSection("- Physical Spoofs -")
task.spawn(function()
	playerTab:CreateToggle({
		Name = "Infinite Jump",
		CurrentValue = false,
		Callback = function(value)
			infjumpenabled = value
			if value then
				ranxConnections:BindConnection("InfiniteJump", game:GetService("UserInputService").JumpRequest:Connect(function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) and not flyenabled or (movementLobbyCheck and mvsd.you.status.inMatch) then
						mvsd.you.humanoid:ChangeState(Enum.HumanoidStateType.Jumping)
					end
				end))
			else
				ranxConnections:UnbindConnection("InfiniteJump")
			end
		end
	})
end)
task.spawn(function()
	local spinbotSpeed = {CurrentValue = 10}
	playerTab:CreateToggle({
		Name = "Spinbot",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("Spinbot", function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
						if (movementLobbyCheck and not mvsd.you.status.inMatch) then return end
						local vel = mvsd.you.character.HumanoidRootPart.RotVelocity
						mvsd.you.character.HumanoidRootPart.RotVelocity = Vector3.new(vel.X, spinbotSpeed.CurrentValue, vel.Z)
					end
				end)
			else
				ranxConnections:UnbindConnection("Spinbot")
			end
		end
	})
	spinbotSpeed = playerTab:CreateSlider({
		Name = "Spinbot Speed",
		Range = {2,100},
		Increment = 2,
		CurrentValue = 10,
		Callback = function() end
	})
end)
task.spawn(function()
	local bhopHeight = {CurrentValue = 25}
	local bhopAlways = {CurrentValue = false}
	local bhopLegit = {CurrentValue = false}
	playerTab:CreateToggle({
		Name = "Bunny Hop",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("Bunnyhop", function()
					if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
						if flyenabled or infjumpenabled or (movementLobbyCheck and not mvsd.you.status.inMatch) then return end
						if mvsd.you.humanoid.FloorMaterial ~= Enum.Material.Air then
							if bhopAlways.CurrentValue or (not bhopAlways.CurrentValue and mvsd.you.humanoid.MoveDirection ~= Vector3.new()) then
								if bhopLegit.CurrentValue then
									mvsd.you.humanoid:ChangeState(Enum.HumanoidStateType.Jumping)
								else
									mvsd.you.character.HumanoidRootPart.Velocity = Vector3.new(mvsd.you.character.HumanoidRootPart.Velocity.X, bhopHeight.CurrentValue, mvsd.you.character.HumanoidRootPart.Velocity.Z)
								end
							end
						end
					end
				end)
			else
				ranxConnections:UnbindConnection("Bunnyhop")
			end
		end
	})
	bhopHeight = playerTab:CreateSlider({
		Name = "BHop Height",
		Range = {15,40},
		CurrentValue = 25,
		Increment = 5,
		Callback = function() end
	})
	bhopAlways = playerTab:CreateToggle({
		Name = "Always BHop",
		CurrentValue = false,
		Callback = function() end
	})
	bhopLegit = playerTab:CreateToggle({
		Name = "Legit BHop",
		CurrentValue = false,
		Callback = function() end
	})
end)

-- VISUALS --
local enemyColor = Color3.new(1,0,0)

local function WorldToViewportPoint(position)
	local pos,vis = workspace.CurrentCamera:WorldToViewportPoint(workspace.CurrentCamera.CFrame:PointToWorldSpace(workspace.CurrentCamera.CFrame:PointToObjectSpace(position)))
	return pos,vis
end
local function WorldToScreenPoint(position)
	local pos,vis = workspace.CurrentCamera:WorldToScreenPoint(workspace.CurrentCamera.CFrame:PointToWorldSpace(workspace.CurrentCamera.CFrame:PointToObjectSpace(position)))
	return pos,vis
end

visualsTab:CreateSection("- ESP Configs -")
task.spawn(function()
	local boxTable = {}
	local function EstimatePosition(pos)
		return Vector2.new(math.floor(pos.X), math.floor(pos.Y))
	end
	local function updateBoxColor(box,color)
		if box then
			box.color = color
			box.nodes.Quad1.Color = box.color or color
		end
	end
	local function updateBoxVisibility(box,visible)
		if box then
			box.visible = visible
			box.nodes.Quad1.Visible = box.visible or visible
			box.nodes.QuadLine2.Visible = box.visible or visible
			box.nodes.QuadLine3.Visible = box.visible or visible
		end
	end
	local function drawBox(player)
		local box = {
			visible = true,
			color = Color3.new(),
			nodes = {}
		}
		box.nodes.Quad1 = Drawing.new("Square")
		box.nodes.Quad1.Visible = box.visible
		box.nodes.Quad1.ZIndex = 2
		box.nodes.Quad1.Filled = false
		box.nodes.Quad1.Thickness = 1
		box.nodes.Quad1.Transparency = 1
		box.nodes.Quad1.Color = box.color
		box.nodes.QuadLine2 = Drawing.new("Square")
		box.nodes.QuadLine2.ZIndex = 1
		box.nodes.QuadLine2.Thickness = 1
		box.nodes.QuadLine2.Filled = false
		box.nodes.QuadLine2.Transparency = 0.5
		box.nodes.QuadLine2.Visible = box.visible
		box.nodes.QuadLine2.Color = Color3.new()
		box.nodes.QuadLine3 = Drawing.new("Square")
		box.nodes.QuadLine3.ZIndex = 1
		box.nodes.QuadLine3.Thickness = 1
		box.nodes.QuadLine3.Filled = false
		box.nodes.QuadLine3.Transparency = 0.5
		box.nodes.QuadLine3.Visible = box.visible
		box.nodes.QuadLine3.Color = Color3.new()
		box.Destroy = function()
			for _,i in next,box.nodes do
				i:Remove()
			end
		end
		boxTable[player.Name] = box
	end
	local function MoveBoxes()
		for username,box in next,boxTable do
			local player = game.Players:FindFirstChild(username)
			if player and player.Character and player.Character.Parent == workspace then
				local root = player.Character:FindFirstChild("HumanoidRootPart")
				if root then
					local rootPosition, rootVisible = WorldToViewportPoint(root.Position)
					if not rootVisible then
						updateBoxVisibility(box,false)
						continue
					end
					if not player:GetAttribute("Match") or player:GetAttribute("Match") ~= mvsd.you.status.matchId or not funcs.isAlive(player.Character) then
						updateBoxVisibility(box,false)
						continue
					end
					if player.Character.Parent == game.ReplicatedStorage.HiddenCharacters then
						updateBoxVisibility(box,false)
						continue
					end
					local topPosition = WorldToViewportPoint((CFrame.new(root.Position, root.Position + workspace.CurrentCamera.CFrame.LookVector) * CFrame.new(2,3,0)).Position)
					local bottomPosition = WorldToViewportPoint((CFrame.new(root.Position, root.Position + workspace.CurrentCamera.CFrame.LookVector) * CFrame.new(-2,-3.5,0)).Position)
					local relSizeX, relSizeY = (topPosition.X - bottomPosition.X), (topPosition.Y - bottomPosition.Y)
					local relPositionX, relPositionY = (rootPosition.X - relSizeX / 2), (rootPosition.Y - relSizeY / 2)
					box.nodes.Quad1.Position = EstimatePosition(Vector2.new(relPositionX,relPositionY))
					box.nodes.Quad1.Size = EstimatePosition(Vector2.new(relSizeX,relSizeY))
					box.nodes.QuadLine2.Position = EstimatePosition(Vector2.new(relPositionX - 1,relPositionY + 1))
					box.nodes.QuadLine2.Size = EstimatePosition(Vector2.new(relSizeX + 2,relSizeY - 2))
					box.nodes.QuadLine3.Position = EstimatePosition(Vector2.new(relPositionX + 1,relPositionY - 1))
					box.nodes.QuadLine3.Size = EstimatePosition(Vector2.new(relSizeX - 2,relSizeY + 2))
					updateBoxVisibility(box, true)
					if mvsd.you.status.inMatch then
						if funcs.getTeamStatus(player) == "Enemy" then
							updateBoxVisibility(box, true)
							updateBoxColor(box, enemyColor)
						elseif funcs.getTeamStatus(player) == "Ally" then
							updateBoxVisibility(box, false)
						end
					else
						updateBoxVisibility(box, false)
					end
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Box ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					for _,i in next,game.Players:GetPlayers() do
						if i ~= mvsd.you.lplr then
							drawBox(i)
						end
					end
					ranxConnections:BindConnection("DrawBoxOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
						if not boxTable[plr.Name] then drawBox(plr) end
					end))
					ranxConnections:BindConnection("RemoveBoxOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
						if boxTable[plr.Name] then boxTable[plr.Name].Destroy() end
					end))
					ranxConnections:BindToRenderStep("BoxMoveLoop",function()
						MoveBoxes()
					end)
				end)
			else
				task.spawn(function()
					ranxConnections:UnbindConnection("DrawBoxOnEntityAdded")
					ranxConnections:UnbindConnection("RemoveBoxOnEntityRemoval")
					ranxConnections:UnbindConnection("BoxMoveLoop")
					for _,box in next,boxTable do
						box.Destroy()
					end
				end)
			end
		end
	})
end)
task.spawn(function()
	local chamsTable = {}
	local function drawHighlight(player)
		local highlight = Instance.new("Highlight", game.CoreGui)
		highlight.Adornee = player.Character
		highlight.DepthMode = Enum.HighlightDepthMode.AlwaysOnTop
		chamsTable[player.Name] = highlight
	end
	local function renewAdornee(player)
		local cham = chamsTable[player.Name]
		if cham then cham.Adornee = player.Character end
	end
	local function renewHighlights()
		for username,cham in next,chamsTable do
			local player = game.Players:FindFirstChild(username)
			if player and player.Character and player.Character.Parent == workspace then
				renewAdornee(player)
				local root = player.Character:FindFirstChild("HumanoidRootPart")
				if root then
					local _,rootVisible = WorldToViewportPoint(root.Position)
					if not rootVisible then
						cham.Enabled = false
						continue
					end
					if not player:GetAttribute("Match") or player:GetAttribute("Match") ~= mvsd.you.status.matchId or not funcs.isAlive(player.Character) then
						cham.Enabled = false
						continue
					end
					if player.Character.Parent == game.ReplicatedStorage.HiddenCharacters then
						cham.Enabled = false
						continue
					end
					cham.Enabled = true
					if mvsd.you.status.inMatch then
						if funcs.getTeamStatus(player) == "Enemy" then
							cham.Enabled = true
							cham.FillColor = enemyColor
							cham.OutlineColor = enemyColor
						elseif funcs.getTeamStatus(player) == "Ally" then
							cham.Enabled = false
						end
					else
						cham.Enabled = false
					end
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Chams ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					for _,i in next,game.Players:GetPlayers() do
						if i ~= mvsd.you.lplr then
							drawHighlight(i)
						end
					end
					ranxConnections:BindConnection("DrawChamsOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
						if not chamsTable[plr.Name] then drawHighlight(plr) end
					end))
					ranxConnections:BindConnection("RemoveChamsOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
						if chamsTable[plr.Name] then chamsTable[plr.Name]:Destroy() end
					end))
					ranxConnections:BindToRenderStep("ChamsUpdateLoop",function()
						renewHighlights()
					end)
				end)
			else
				task.spawn(function()
					for _,highlight in next,chamsTable do
						ranxConnections:UnbindConnection("DrawChamsOnEntityAdded")
						ranxConnections:UnbindConnection("RemoveChamsOnEntityRemoval")
						ranxConnections:UnbindConnection("ChamsUpdateLoop")
						highlight:Destroy()
					end
				end)
			end
		end
	})
end)
task.spawn(function()
	local tracersTable = {}
	local tracerOrigin = {CurrentOption = {"Bottom"}}

	local function drawTracer(player)
		local tracer = Drawing.new("Line")
		tracer.Thickness = 2
		tracersTable[player.Name] = tracer
	end
	local function renewTracers()
		for username,tracer in next,tracersTable do
			local player = game.Players:FindFirstChild(username)
			if player and player.Character and player.Character.Parent == workspace then
				local root = player.Character:FindFirstChild("HumanoidRootPart")
				if root then
					local rootPosition,rootVisible = WorldToViewportPoint(root.Position)
					if not rootVisible then
						tracer.Visible = false
						continue
					end
					if not player:GetAttribute("Match") or player:GetAttribute("Match") ~= mvsd.you.status.matchId or not funcs.isAlive(player.Character) then
						tracer.Visible = false
						continue
					end
					if player.Character.Parent == game.ReplicatedStorage.HiddenCharacters then
						tracer.Visible = false
						continue
					end
					local resolution = workspace.CurrentCamera.ViewportSize
					if tracerOrigin.CurrentOption[1] == "Bottom" then
						tracer.From = Vector2.new(resolution.X / 2,resolution.Y)
					elseif tracerOrigin.CurrentOption[1] == "Top" then
						tracer.From = Vector2.new(resolution.X / 2,0)
					elseif tracerOrigin.CurrentOption[1] == "Mouse" then
						tracer.From = funcs.getMouseLocation()
					elseif tracerOrigin.CurrentOption[1] == "Center" then
						tracer.From = Vector2.new(resolution.X / 2,resolution.Y / 2)
					end
					tracer.To = rootPosition
					tracer.Visible = true
					if mvsd.you.status.inMatch then
						if funcs.getTeamStatus(player) == "Enemy" then
							tracer.Visible = true
							tracer.Color = enemyColor
						elseif funcs.getTeamStatus(player) == "Ally" then
							tracer.Visible = false
						end
					else
						tracer.Visible = false
					end
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Tracers ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					for _,i in next,game.Players:GetPlayers() do
						if i ~= mvsd.you.lplr then
							drawTracer(i)
						end
					end
					ranxConnections:BindConnection("DrawTracersOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
						if not tracersTable[plr.Name] then drawTracer(plr) end
					end))
					ranxConnections:BindConnection("RemoveTracersOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
						if tracersTable[plr.Name] then tracersTable[plr.Name]:Destroy() end
					end))
					ranxConnections:BindToRenderStep("TracerUpdateLoop",function()
						renewTracers()
					end)
				end)
			else
				task.spawn(function()
					for _,tracer in next,tracersTable do
						ranxConnections:UnbindConnection("DrawTracersOnEntityAdded")
						ranxConnections:UnbindConnection("RemoveTracersOnEntityRemoval")
						ranxConnections:UnbindConnection("TracerUpdateLoop")
						tracer:Destroy()
					end
				end)
			end
		end
	})
	tracerOrigin = visualsTab:CreateDropdown({
		Name = "Tracer Origin",
		Options = {"Bottom","Top","Mouse","Center"},
		CurrentOption = {"Bottom"},
		Callback = function() end
	})
end)
task.spawn(function()
	local nametagsTable = {}
	local tagTexts = {}
	local nametagsDisplayNames = {CurrentValue = true}
	local nametagsScale = {CurrentValue = 10}
	local nametagsDistance = {CurrentValue = false}
	local nametagsUI = Instance.new("ScreenGui", game.CoreGui)
	nametagsUI.Name = "ranxtags"
	local function drawNametag(player)
		local tag = Instance.new("TextLabel",nametagsUI)
		tag.BackgroundColor3 = Color3.new()
		tag.Visible = false
		tag.RichText = true
		tag.AnchorPoint = Vector2.new(0.5,1)
		tag.Font = Enum.Font.GothamMedium
		tag.BackgroundTransparency = 0.5
		tagTexts[player.Name] = player.Name
		nametagsTable[player.Name] = tag
	end
	local function updateNametags()
		for username,tag in next,nametagsTable do
			local player = game.Players:FindFirstChild(username)
			if player and player.Character and player.Character.Parent == workspace then
				local root = player.Character:FindFirstChild("HumanoidRootPart")
				local head = player.Character:FindFirstChild("Head")
				if root and head then
					local rootPosition,rootVisible = WorldToViewportPoint(root.Position)
					local headPosition = WorldToScreenPoint((head.CFrame * CFrame.new(0, head.Size.Y + root.Size.Y + 2,0)).Position)
					if not rootVisible then
						tag.Visible = false
						continue
					end
					if not player:GetAttribute("Match") or player:GetAttribute("Match") ~= mvsd.you.status.matchId or not funcs.isAlive(player.Character) then
						tag.Visible = false
						continue
					end
					if player.Character.Parent == game.ReplicatedStorage.HiddenCharacters then
						tag.Visible = false
						continue
					end
					tagTexts[player.Name] = nametagsDisplayNames.CurrentValue and player.DisplayName or player.Name
					if nametagsDistance.CurrentValue then
						local suc,distance = pcall(function() return (mvsd.you.character:FindFirstChild("HumanoidRootPart").Position - root.Position).Magnitude end)
						if suc then
							tagTexts[player.Name] = tagTexts[player.Name] .. ' <font color="rgb(100,255,100)">[</font><font color="rgb(255,255,255)">'..math.floor(distance)..'</font><font color="rgb(100,255,100)">]</font>'
						end
					end
					local relativeSize = game:GetService("TextService"):GetTextSize(string.gsub(string.gsub(tagTexts[player.Name], "<br%s*/>","\n"),"<[^<>]->", ""), 14 * (nametagsScale.CurrentValue / 10), Enum.Font.GothamMedium, Vector2.new(1e5,1e5))
					tag.TextSize = 14 * (nametagsScale.CurrentValue / 10)
					tag.Size = UDim2.new(0,relativeSize.X + 4,0,relativeSize.Y)
					tag.Position = UDim2.new(0,headPosition.X,0,headPosition.Y)
					tag.Visible = true
					if mvsd.you.status.inMatch then
						if funcs.getTeamStatus(player) == "Enemy" then
							tag.Visible = true
							tag.TextColor3 = enemyColor
						elseif funcs.getTeamStatus(player) == "Ally" then
							tag.Visible = false
						end
					else
						tag.Visible = false
					end
					tag.Text = tagTexts[player.Name]
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Nametags ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					for _,i in next,game.Players:GetPlayers() do
						if i ~= mvsd.you.lplr then
							drawNametag(i)
						end
					end
					ranxConnections:BindConnection("DrawNametagsOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
						if not nametagsTable[plr.Name] then drawNametag(plr) end
					end))
					ranxConnections:BindConnection("RemoveNametagsOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
						if nametagsTable[plr.Name] then nametagsTable[plr.Name]:Destroy() end
					end))
					ranxConnections:BindToRenderStep("NamtagUpdateLoop",function()
						updateNametags()
					end)
				end)
			else
				task.spawn(function()
					ranxConnections:UnbindConnection("DrawNametagsOnEntityAdded")
					ranxConnections:UnbindConnection("RemoveNametagsOnEntityRemoval")
					ranxConnections:UnbindConnection("NamtagUpdateLoop")
					for _,tracer in next,nametagsTable do
						tracer:Destroy()
					end
				end)
			end
		end
	})
	nametagsDisplayNames = visualsTab:CreateToggle({
		Name = "Use Display Names",
		CurrentValue = true,
		Callback = function() end
	})
	nametagsDistance = visualsTab:CreateToggle({
		Name = "Distance ESP",
		CurrentValue = false,
		Callback = function() end
	})
	nametagsScale = visualsTab:CreateSlider({
		Name = "Nametags Scale",
		Range = {4,18},
		CurrentValue = 10,
		Increment = 1,
		Callback = function() end
	})
end)

visualsTab:CreateSection("- ESP Settings -")
task.spawn(function()
	visualsTab:CreateColorPicker({
		Name = "Enemy Color",
		Color = Color3.new(1,0,0),
		Callback = function(value)
			enemyColor = value
		end
	})
end)

visualsTab:CreateSection("- World Rendering -")
task.spawn(function()
	local oldLighting = {
		Brightness = 3,
		Ambient = Color3.fromRGB(70,70,70),
		OutdoorAmbient = Color3.fromRGB(70,70,70),
		GlobalShadows = true,
		ClockTime = 14.5
	}

	local fullbright = {CurrentValue = false}
	fullbright = visualsTab:CreateToggle({
		Name = "Fullbright",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					oldLighting.Brightness = game.Lighting.Brightness
					oldLighting.Ambient = game.Lighting.Ambient
					oldLighting.OutdoorAmbient = game.Lighting.OutdoorAmbient
					oldLighting.GlobalShadows = game.Lighting.GlobalShadows
					oldLighting.ClockTime = game.Lighting.ClockTime
					repeat
						game.Lighting.Brightness = 3
						game.Lighting.Ambient = Color3.fromRGB(120,120,120)
						game.Lighting.OutdoorAmbient = Color3.fromRGB(120,120,120)
						game.Lighting.GlobalShadows = false
						game.Lighting.ClockTime = 12
						task.wait()
					until not fullbright.CurrentValue
				end)
			else
				task.spawn(function()
					for property,value in next,oldLighting do
						game.Lighting[property] = value
					end
				end)
			end
		end
	})
end)

visualsTab:CreateSection("- Weapon Rendering -")
task.spawn(function()
	local materialTable = {
		"Plastic",
		"ForceField",
		"Brick",
		"Neon",
		"Granite",
		"Metal",
		"Glass"
	}
	local rainbow = Color3.new()
	local customWeapon = {CurrentValue = false}
	local customWeaponColor = {Color = Color3.new(1,1,1)}
	local customWeaponMaterial = {CurrentOption = {"Plastic"}}
	local customWeaponTransparency = {CurrentValue = 0}
	local customWeaponRainbow = {CurrentValue = false}
	customWeapon = visualsTab:CreateToggle({
		Name = "Custom Weapon Skins",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("RainbowColor", function()
					rainbow = Color3.fromHSV(tick() % 5 / 5, 1, 1)
				end)
				task.spawn(function()
					repeat
						if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
							local knife = funcs.getKnife()
							local revolver = funcs.getRevolver()
							if knife then
								if knife.Parent == mvsd.you.character then
									local rightHandle = knife:FindFirstChild("RightHandle")
									local leftHandle = knife:FindFirstChild("LeftHandle")
									if rightHandle then
										rightHandle.TextureID = ""
										rightHandle.Color = customWeaponRainbow.CurrentValue and rainbow or customWeaponColor.Color
										rightHandle.Material = customWeaponMaterial.CurrentOption[1]
										rightHandle.Transparency = customWeaponTransparency.CurrentValue
									end
									if leftHandle then
										leftHandle.TextureID = ""
										leftHandle.Color = customWeaponRainbow.CurrentValue and rainbow or customWeaponColor.Color
										leftHandle.Material = customWeaponMaterial.CurrentOption[1]
										leftHandle.Transparency = customWeaponTransparency.CurrentValue
									end
								end
							end
							if revolver then
								if revolver.Parent == mvsd.you.character then
									local rightHandle = revolver:FindFirstChild("RightHandle")
									local leftHandle = revolver:FindFirstChild("LeftHandle")
									if rightHandle then
										rightHandle.TextureID = ""
										rightHandle.Color = customWeaponRainbow.CurrentValue and rainbow or customWeaponColor.Color
										rightHandle.Material = customWeaponMaterial.CurrentOption[1]
										rightHandle.Transparency = customWeaponTransparency.CurrentValue
									end
									if leftHandle then
										leftHandle.TextureID = ""
										leftHandle.Color = customWeaponRainbow.CurrentValue and rainbow or customWeaponColor.Color
										leftHandle.Material = customWeaponMaterial.CurrentOption[1]
										leftHandle.Transparency = customWeaponTransparency.CurrentValue
									end
								end
							end
						end
						task.wait()
					until not customWeapon.CurrentValue
				end)
			else
				ranxConnections:UnbindConnection("RainbowColor")
			end
		end
	})
	customWeaponColor = visualsTab:CreateColorPicker({
		Name = "Weapon Color",
		Color = Color3.new(1,1,1),
		Callback = function() end
	})
	customWeaponMaterial = visualsTab:CreateDropdown({
		Name = "Weapon Material",
		Options = materialTable,
		CurrentOption = {"Metal"},
		Callback = function() end
	})
	customWeaponTransparency = visualsTab:CreateSlider({
		Name = "Weapon Transparency",
		Range = {0,1},
		CurrentValue = 0,
		Increment = 0.05,
		Callback = function() end
	})
	customWeaponRainbow = visualsTab:CreateToggle({
		Name = "Rainbow Weapon",
		CurrentValue = false,
		Callback = function() end
	})
end)

-- MISC --

-- useless features, will need to update them soon
--[[miscTab:CreateSection("- Automated Configs -")
task.spawn(function()
	local autochoose = {CurrentValue = false}
	local autochooseWeapon = {CurrentOption = {"Murderer"}}
	local roleSelectionUI = mvsd.you.lplr.PlayerGui.RoleSelection
	autochoose = miscTab:CreateToggle({
		Name = "Auto-Choose Role",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if roleSelectionUI and roleSelectionUI.Enabled then
							if mvsd.you.status.inMatch and funcs.getGamemode() == "Classic" then
								task.wait(0.5)
								mvsd.remotes.onRoleSelection:FireServer(unpack({
									[1] = autochooseWeapon.CurrentOption[1]
								}))
								task.wait()
								roleSelectionUI.Enabled = false
							end
						end
						task.wait(0.1)
					until not autochoose.CurrentValue
				end)
			end
		end
	})
	autochooseWeapon = miscTab:CreateDropdown({
		Name = "Chosen Role",
		Options = {"Murderer","Sheriff"},
		CurrentOption = {"Murderer"},
		Callback = function() end
	})
end)
task.spawn(function()
	local autoqueueGamemode = {CurrentOption = {"Duality"}}
	local autoqueueType = {CurrentOption = {"1v1"}}
	local function getDualityStands()
		local stands = {}
		for _,i in next,game:GetService("CollectionService"):GetTagged("MatchStand") do
			if i:IsA("BasePart") and i.Name == "Stand" then
				local container = i.Parent
				if container then
					local vs = container:FindFirstChild("vs")
					if vs then
						local image = vs.SurfaceGui:FindFirstChildOfClass("ImageLabel")
						if image and string.find(image.Image, "12892376379") then
							table.insert(stands, container)
						end
					end
				end
			end
		end
		return stands
	end
	local function getClassicStands()
		local stands = {}
		for _,i in next,game:GetService("CollectionService"):GetTagged("MatchStand") do
			if i:IsA("BasePart") and i.Name == "Stand" then
				local container = i.Parent
				if container then
					local vs = container:FindFirstChild("vs")
					if vs then
						local image = vs.SurfaceGui:FindFirstChildOfClass("ImageLabel")
						if image and string.find(image.Image, "12892422950") then
							table.insert(stands, container)
						end
					end
				end
			end
		end
		return stands
	end
	local function getSpawns(container)
		local stands = {}
		for _,i in next,container:GetChildren() do
			if i:IsA("BasePart") and i.Name == "Stand" then
				table.insert(stands, i)
			end
		end
		return stands
	end
	local function getPlayersThreshold(stand)
		local amount = 0
		local occupied = 0
		local allOccupied = false
		local obj = {}
		for _,i in next,stand:GetChildren() do
			if i:IsA("ObjectValue") and i.Name == "StandCharacter" then
				table.insert(obj, i)
			end
		end
		amount = #obj
		for _,i in next,obj do
			if i.Value and game.Players:GetPlayerFromCharacter(i.Value) then
				occupied = occupied + 1
			end
		end
		if occupied >= amount then allOccupied = true end
		return amount, allOccupied
	end
	miscTab:CreateButton({
		Name = "TP to Match Stand",
		Callback = function()
			if not currentlyRespawning and funcs.isAlive(mvsd.you.character) then
				if not mvsd.you.status.inMatch then
					local stands = getDualityStands()
					if autoqueueGamemode.CurrentOption[1] == "Classic" then
						stands = getClassicStands()
					end
					local s = {}
					for _,i in next,stands do
						local m = getSpawns(i)
						for _,t in next,m do
							local amount = getPlayersThreshold(t)
							local n = tonumber(string.sub(autoqueueType.CurrentOption[1], 1, 1))
							if amount == n then
								table.insert(s, t)
							end
						end
					end
					local a = {}
					for _,h in next,s do
						local _, occupied = getPlayersThreshold(h)
						if not occupied then
							table.insert(a, h)
						end
					end
					if #a <= 0 then
						lib:Notify({
							Title = "Ranxware Error",
							Content = "That match stand is already occupied! Please wait until it is available.",
							Duration = 5
						})
						return
					end
					mvsd.you.character.HumanoidRootPart.CFrame = a[math.random(1, #a)].CFrame * CFrame.new(0,4,0)
				end
			end
		end
	})
	autoqueueGamemode = miscTab:CreateDropdown({
		Name = "Queue Gamemode",
		Options = {"Duality","Classic"},
		CurrentOption = {"Duality"},
		Callback = function() end
	})
	autoqueueType = miscTab:CreateDropdown({
		Name = "Queue Type",
		Options = {"1v1","2v2","4v4"},
		CurrentOption = {"1v1"},
		Callback = function() end
	})
end)]]

miscTab:CreateSection("- Trolling Configs -")
task.spawn(function()
	local knifespam = {CurrentValue = false}
	knifespam = miscTab:CreateToggle({
		Name = "Knife Spammer",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						local knife = funcs.getKnife()
						if knife then
							for _,plr in next,funcs.getEnemies() do
								if funcs.isAlive(plr.Character) then
									task.wait(0.05)
									local rootcframe = plr.Character.HumanoidRootPart.CFrame
									mvsd.remotes.ThrowStart:FireServer(unpack({
										[1] = rootcframe.Position,
										[2] = rootcframe.UpVector
									}))
								end
							end
						end
						task.wait()
					until not knifespam.CurrentValue
				end)
			end
		end
	})
end)

miscTab:CreateSection("- Script Settings -")
task.spawn(function()
	miscTab:CreateToggle({
		Name = "Movement Lobby Check",
		CurrentValue = true,
		Callback = function(value)
			movementLobbyCheck = value
		end
	})
end)
