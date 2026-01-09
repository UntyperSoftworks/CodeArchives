-- Ranxware V2: REVIVED | Murderers vs Sheriffs
-- (new script rewrite i guess)

if not shared.ranxwareExecuted then return end

local whitelist = shared.whitelist
whitelist.updatePriority()

local mvs = {
	remotes = {
		spawnProtectionEvent = game.ReplicatedStorage.Remotes.SpawnProtection,
		replicateBulletEvent = game.ReplicatedStorage.Remotes.ReplicateBullet,
		replicateKnifeEvent = game.ReplicatedStorage.Remotes.ReplicateKnife,
		displayKillDeath = game.ReplicatedStorage.Remotes.DisplayKillDeath,
		damageEvent = nil -- damage event placeholder
	},
	you = {
		lplr = game.Players.LocalPlayer,
		character = game.Players.LocalPlayer.Character,
		humanoid = game.Players.LocalPlayer.Character and game.Players.LocalPlayer.Character:FindFirstChildOfClass("Humanoid"),
		status = {
			team = nil,
			playing = game.ReplicatedStorage.Values.Playing.Value
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
	getEnemies = function()
		local enemies = {}
		for _,plr in next,game.Players:GetPlayers() do
			if plr ~= mvs.you.lplr and plr.Team.Name ~= "Neutral" and plr.Team ~= mvs.you.lplr.Team then
				table.insert(enemies, plr)
			end
		end
		return enemies
	end,
	getAllies = function()
		local teammates = {}
		for _,plr in next,game.Players:GetPlayers() do
			if plr ~= mvs.you.lplr and plr.Team.Name ~= "Neutral" and plr.Team == mvs.you.lplr.Team then
				table.insert(teammates, plr)
			end
		end
		return teammates
	end,
	getRoleStatus = function(plr)
		return plr.Team.Name
	end,
	isAlive = function(character)
		return character and character:FindFirstChildOfClass("Humanoid") and character:FindFirstChildOfClass("Humanoid").Health > 0
	end,
	hasForcefield = function(character)
		return character:FindFirstChild("ForceField") ~= nil
	end,
	getPlayerNearestCursor = function(size, hitpart)
		local closest = size
		local target = nil
		for _,plr in next,game.Players:GetPlayers() do
			if plr ~= mvs.you.lplr then
				if plr.Team ~= mvs.you.status.team and plr.Team.Name ~= "Neutral" then
					if plr.Character and plr.Character:FindFirstChildOfClass("Humanoid") and plr.Character:FindFirstChildOfClass("Humanoid").Health > 0 and not plr.Character:FindFirstChild("ForceField") and plr.Character:FindFirstChild(hitpart) then
						local vector = workspace.CurrentCamera:WorldToViewportPoint(plr.Character:FindFirstChild(hitpart).Position)
						if vector then
							local magnitude = (game:GetService("UserInputService"):GetMouseLocation() - Vector2.new(vector.X,vector.Y)).Magnitude
							if magnitude <= closest then
								closest = magnitude
								target = plr
							end
						end
					end
				end
			end
		end
		return target
	end,
	getPlayersNearPosition = function(size, localpos)
		local closest = size
		local targets = {}
		local nearest = nil
		for _,plr in next,game.Players:GetPlayers() do
			if plr ~= mvs.you.lplr then
				if plr.Team ~= mvs.you.status.team and plr.Team.Name ~= "Neutral" then
					if plr.Character and plr.Character:FindFirstChildOfClass("Humanoid") and plr.Character:FindFirstChildOfClass("Humanoid").Health > 0 and not plr.Character:FindFirstChild("ForceField") then
						local magnitude = (localpos - plr.Character.HumanoidRootPart.Position).Magnitude
						if magnitude <= size then
							if not table.find(targets, plr.Character) then table.insert(targets, plr) end
							if magnitude <= closest then
								closest = magnitude
								nearest = plr
							end
						end
					end
				end
			end
		end
		return targets, nearest
	end,
	getWeapon = function()
		local currentWeapon = nil
		for _,i in next,mvs.you.lplr.Backpack:GetChildren() do
			if i:IsA("Tool") then
				if i.Name == "Knife" or i.Name == "Gun" then
					currentWeapon = i
				end
			end
		end
		for _,i in next,mvs.you.character:GetChildren() do
			if i:IsA("Tool") then
				if i.Name == "Knife" or i.Name == "Gun" then
					currentWeapon = i
				end
			end
		end
		return currentWeapon
	end,
	isPlaying = function()
		return game.ReplicatedStorage.Values.Playing.Value
	end,
	getMap = function()
		local map = nil
		for _,i in next,game.ReplicatedStorage.MapConfigurations:GetChildren() do
			local realMap = workspace:FindFirstChild(i.Name)
			if realMap and realMap:IsA("Model") and not game.Players:GetPlayerFromCharacter(realMap) then
				map = realMap
			end
		end
		return map
	end
}
funcs.getBarriers = function()
	local barriers = nil
	local map = funcs.getMap()
	if map and map:FindFirstChild("Barriers") then
		barriers = map:FindFirstChild("Barriers")
	end
	return barriers
end

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

local currentlyRespawning = false
ranxConnections:BindToHeartbeat("WeaponChange",function()
	if not currentlyRespawning and funcs.isAlive(mvs.you.character) then
		local weapon = funcs.getWeapon()
		if weapon and weapon:FindFirstChild("Damage") then
			mvs.remotes.damageEvent = weapon:FindFirstChild("Damage")
		else
			mvs.remotes.damageEvent = nil
		end
	end
end)

mvs.you.lplr.CharacterAdded:Connect(function(newchar)
	currentlyRespawning = true
	task.wait(0.3)
	mvs.you.character = newchar
	mvs.you.status.team = mvs.you.lplr.Team
	mvs.you.humanoid = mvs.you.character:FindFirstChildOfClass("Humanoid") or newchar:FindFirstChildOfClass("Humanoid")
	currentlyRespawning = false
end)

local lib = shared.uiLibrary
local window = lib:CreateWindow({
	Name = "Ranxware V2 [ALPHA] | MVS",
	LoadingTitle = "Ranxware V2 REVIVED",
	LoadingSubtitle = "by: AsteriskCodes",
	ConfigurationSaving = {
		Enabled = false
	}
})

local combatTab = window:CreateTab("Combat")
local ragebotTab = window:CreateTab("Ragebot")
local playerTab = window:CreateTab("Player")
local visualsTab = window:CreateTab("Visuals")
local miscTab = window:CreateTab("Misc")

-- COMBAT --
combatTab:CreateSection("- Hitbox Spoofs -")
task.spawn(function()
	local hitboxes = {CurrentValue = false}
	local hitboxPart = {CurrentOption = {"Root"}}
	local hitboxSize = {CurrentValue = 1}
	local rootTransparency = {CurrentValue = 0.8}
	hitboxes = combatTab:CreateToggle({
		Name = "Spoof Hitboxes",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if funcs.isPlaying() then
							for _,plr in next,funcs.getEnemies() do
								local char = plr.Character
								if funcs.isAlive(char) then
									local root = char:FindFirstChild("HumanoidRootPart")
									local head = char:FindFirstChild("Head")
									if head and root then
										local OGRootSize = root:FindFirstChild("OriginalSize") and root:FindFirstChild("OriginalSize").Value or Vector3.new(2,2,1)
										local OGheadsize = head:FindFirstChild("OriginalSize") and head:FindFirstChild("OriginalSize").Value or (char:FindFirstChildOfClass("Humanoid").RigType == Enum.HumanoidRigType.R6 and Vector3.new(2,1,1)) or Vector3.new(1,1,1)
										if hitboxPart.CurrentOption[1] == "Root" then
											root.Size = Vector3.new(1,1,1) * hitboxSize.CurrentValue
											root.Transparency = rootTransparency.CurrentValue
											head.Size = OGheadsize
										elseif hitboxPart.CurrentOption[1] == "Head" then
											head.Size = OGheadsize * hitboxSize.CurrentValue
											root.Size = OGRootSize
											root.Transparency = 1
										end
									end
								end
							end
						end
						task.wait()
					until not hitboxes.CurrentValue
				end)
			else
				for _,plr in next,funcs.getEnemies() do
					local char = plr.Character
					if funcs.isAlive(char) then
						local root = char:FindFirstChild("HumanoidRootPart")
						local head = char:FindFirstChild("Head")
						if head and root then
							local OGRootSize = root:FindFirstChild("OriginalSize") and root:FindFirstChild("OriginalSize").Value or Vector3.new(2,2,1)
							local OGheadsize = head:FindFirstChild("OriginalSize") and head:FindFirstChild("OriginalSize").Value or (char:FindFirstChildOfClass("Humanoid").RigType == Enum.HumanoidRigType.R6 and Vector3.new(2,1,1)) or Vector3.new(1,1,1)
							root.Size = OGRootSize
							root.Transparency = 1
							head.Size = OGheadsize
						end
					end
				end
			end
		end
	})
	hitboxPart = combatTab:CreateDropdown({
		Name = "Hitbox Part",
		Options = {"Root","Head"},
		CurrentOption = {"Head"},
		Callback = function() end
	})
	hitboxSize = combatTab:CreateSlider({
		Name = "Hitbox Size",
		Range = {1,4},
		CurrentValue = 1,
		Increment = 1,
		Callback = function() end
	})
	rootTransparency = combatTab:CreateSlider({
		Name = "Hitbox Root Transparency",
		Range = {0,1},
		CurrentValue = 0.8,
		Increment = 0.05,
		Callback = function() end
	})
end)

combatTab:CreateSection("- Aimbot Configs -")
task.spawn(function()
	local silentAim = {CurrentValue = false}
	local silentAimHitpart = {CurrentOption = {"Head"}}
	local silentAimFOVSize = {CurrentValue = 100}
	local silentAimFOVVisible = {CurrentValue = true}
	local silentAimFOVColor = {Color = Color3.new(1,0,0)}
	local silentAimWallbang = {CurrentValue = false}
	local silentAimAimline = {CurrentValue = false}
	local function getSoundParts()
		local parts = {}
		for _,i in next,workspace:GetChildren() do
			if i:IsA("BasePart") then
				if i.Name == "HitSound" then
					table.insert(parts, i)
				elseif i:FindFirstChildOfClass("Sound") and string.find(i:FindFirstChildOfClass("Sound").Name, "Impact") then
					table.insert(parts, i)
				end
			end
		end
		return parts
	end
	local function getDeadBodies()
		local dead = {}
		for _,i in next,game.Players:GetPlayers() do
			if i ~= mvs.you.lplr and i.Character and i.Character:FindFirstChildOfClass("Humanoid") and i.Character:FindFirstChildOfClass("Humanoid").Health <= 0 then
				table.insert(dead, i.Character)
			end
		end
		return dead
	end
	local silentAimParams = RaycastParams.new()
	silentAimParams.IgnoreWater = true
	silentAimParams.FilterType = Enum.RaycastFilterType.Exclude
	local circle = Drawing.new("Circle")
	circle.Color = Color3.new(1,0,0)
	circle.Thickness = 1
	circle.NumSides = 9e9
	circle.Radius = 100
	circle.Filled = false
	circle.Visible = false
	local aimline = Drawing.new("Line")
	aimline.Color = Color3.new(1,0,0)
	aimline.Thickness = 1
	aimline.Visible = false
	local hook1;hook1 = hookmetamethod(game, "__namecall", function(self, ...)
		local args = {...}
		if silentAim.CurrentValue and not checkcaller() and getnamecallmethod() == "FireServer" then
			if tostring(self) == "ReplicateBullet" then
				local hitpart = "HumanoidRootPart"
				if silentAimHitpart.CurrentOption[1] == "Head" then
					hitpart = "Head"
				end
				if silentAimHitpart.CurrentOption[1] == "Random" and math.random(1, 3) == 1 then
					hitpart = "Head"
				end
				local nearest = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue, hitpart)
				if nearest then
					silentAimParams.FilterDescendantsInstances = {mvs.you.character, unpack(funcs.getAllies()), workspace.ProjectilesAndDebris, getSoundParts(), workspace.CurrentCamera, unpack(getDeadBodies())}
					if funcs.getBarriers() then
						table.insert(silentAimParams.FilterDescendantsInstances, funcs.getBarriers())
					end
					local result = workspace:Raycast(args[1][1], funcs.getAngle(nearest.Character:FindFirstChild(hitpart).CFrame.Position, args[1][1]) * 9e9, silentAimParams)
					if result then
						args[1][2] = result.Position
						if result.Instance.Parent == nearest.Character then
							mvs.remotes.damageEvent:FireServer(unpack({
								[1] = nearest.Character:FindFirstChild(hitpart),
								[2] = Vector3.new(),
								[3] = Vector3.new()
							}))
						end
						if silentAimWallbang.CurrentValue then
							args[1][2] = nearest.Character:FindFirstChild(hitpart)
							mvs.remotes.damageEvent:FireServer(unpack({
								[1] = nearest.Character:FindFirstChild(hitpart),
								[2] = Vector3.new(),
								[3] = Vector3.new()
							}))
						end
					else
						args[1][2] = nearest.Character:FindFirstChild(hitpart)
						mvs.remotes.damageEvent:FireServer(unpack({
							[1] = nearest.Character:FindFirstChild(hitpart),
							[2] = Vector3.new(),
							[3] = Vector3.new()
						}))
					end
				end
			elseif tostring(self) == "ReplicateKnife" then
				if args[1]["Action"] == "CreateKnife" then
					local hitpart = "HumanoidRootPart"
					if silentAimHitpart.CurrentOption[1] == "Head" then
						hitpart = "Head"
					end
					if silentAimHitpart.CurrentOption[1] == "Random" and math.random(1, 3) == 1 then
						hitpart = "Head"
					end
					local nearest = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue, hitpart)
					if nearest then
						silentAimParams.FilterDescendantsInstances = {mvs.you.character, unpack(funcs.getAllies()), workspace.ProjectilesAndDebris, getSoundParts(), unpack(getDeadBodies())}
						if funcs.getBarriers() then
							table.insert(silentAimParams.FilterDescendantsInstances, funcs.getBarriers())
						end
						local result = workspace:Raycast(args[1]["StartPosition"], funcs.getAngle(nearest.Character:FindFirstChild(hitpart).CFrame.Position, args[1]["StartPosition"]) * 9e9, silentAimParams)
						if result then
							args[1]["EndPosition"] = result.Position
							if result.Instance.Parent == nearest.Character then
								mvs.remotes.damageEvent:FireServer(unpack({
									[1] = nearest.Character:FindFirstChild(hitpart),
									[2] = "Throw"
								}))
							end
							if silentAimWallbang.CurrentValue then
								args[1]["EndPosition"] = nearest.Character:FindFirstChild(hitpart).Position
								mvs.remotes.damageEvent:FireServer(unpack({
									[1] = nearest.Character:FindFirstChild(hitpart),
									[2] = "Throw"
								}))
							end
						else
							args[1]["EndPosition"] = nearest.Character:FindFirstChild(hitpart).Position
							mvs.remotes.damageEvent:FireServer(unpack({
								[1] = nearest.Character:FindFirstChild(hitpart),
								[2] = "Throw"
							}))
						end
					end
				end
			end
			return self.FireServer(self, unpack(args))
		end
		return hook1(self, ...)
	end)
	silentAim = combatTab:CreateToggle({
		Name = "Silent-Aim",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToRenderStep("FOVCircleLoop", function()
					circle.Visible = silentAim.CurrentValue and silentAimFOVVisible.CurrentValue
					circle.Radius = silentAimFOVSize.CurrentValue
					circle.Position = funcs.getMouseLocation()
					circle.Color = silentAimFOVColor.Color
					if silentAimAimline.CurrentValue and funcs.isPlaying() then
						aimline.From = funcs.getMouseLocation()
						aimline.Color = silentAimFOVColor.Color
						local hitpart = "HumanoidRootPart"
						if silentAimHitpart.CurrentOption[1] == "Head" then
							hitpart = "Head"
						end
						local nearest = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue, hitpart)
						if nearest then
							local vector, visible = workspace.CurrentCamera:WorldToViewportPoint(nearest.Character:FindFirstChild(hitpart).Position)
							aimline.Visible = visible
							aimline.To = Vector2.new(vector.X, vector.Y)
						else
							aimline.Visible = false
						end
					else
						aimline.Visible = false
					end
				end)
			else
				ranxConnections:UnbindConnection("FOVCircleLoop")
				circle.Visible = false
				aimline.Visible = false
			end
		end
	})
	silentAimHitpart = combatTab:CreateDropdown({
		Name = "Hitpart",
		Options = {"Head", "Root", "Random"},
		CurrentOption = {"Head"},
		Callback = function() end
	})
	silentAimFOVSize = combatTab:CreateSlider({
		Name = "FOV Size",
		Range = {10, 1000},
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
	silentAimWallbang = combatTab:CreateToggle({
		Name = "Silent-Aim Wallbang",
		CurrentValue = false,
		Callback = function() end
	})
	silentAimAimline = combatTab:CreateToggle({
		Name = "Show Aimline",
		CurrentValue = false,
		Callback = function() end
	})
end)

local killTick = tick()
combatTab:CreateSection("- Weapon Exploits -")
task.spawn(function()
	local knifeReach = {CurrentValue = false}
	local knifeReachValue = {CurrentValue = 4}
	local hook2;hook2 = hookmetamethod(game, "__namecall", function(self,...)
		local args = {...}
		if knifeReach.CurrentValue and not checkcaller() and tostring(self) == "ReplicateKnife" and getnamecallmethod() == "FireServer" then
			if args[1]["Action"] == "PlayKnifeSound" and args[1]["KnifeAction"] == "Slash" then
				if funcs.isAlive(mvs.you.character) then
					for _,plr in next,funcs.getEnemies() do
						if funcs.isAlive(plr.Character) and not funcs.hasForcefield(plr.Character) then
							if (plr.Character.HumanoidRootPart.Position - mvs.you.character.HumanoidRootPart.Position).Magnitude <= knifeReachValue.CurrentValue then
								local dir = mvs.you.character.HumanoidRootPart.CFrame.LookVector:Dot(funcs.getAngle(plr.Character.HumanoidRootPart.Position, mvs.you.character.HumanoidRootPart.Position))
								if math.acos(dir) <= (math.rad(90) / 2) then
									mvs.remotes.damageEvent:FireServer(unpack({
										[1] = plr.Character.HumanoidRootPart,
										[2] = "Slash"
									}))
									task.wait()
								end
							end
						end
					end
				end
			end
			return self.FireServer(self, unpack(args))
		end
		return hook2(self, ...)
	end)
	knifeReach = combatTab:CreateToggle({
		Name = "Spoof Knife Reach",
		CurrentValue = false,
		Callback = function() end
	})
	knifeReachValue = combatTab:CreateSlider({
		Name = "Knife Reach Range",
		Range = {4, 30},
		CurrentValue = 4,
		Increment = 1,
		Suffix = "studs",
		Callback = function() end
	})
end)

-- RAGEBOT --
ragebotTab:CreateSection("- Killaura Exploits -")
local killallenabled = false
task.spawn(function()
	local part = Instance.new("Part", workspace.CurrentCamera)
	part.Transparency = 1
	part.CanCollide = false
	part.Anchored = false
	part.Size = Vector3.new(1,1,1)
	local range = Instance.new("CylinderHandleAdornment", game.CoreGui)
	range.Color3 = Color3.new(1,0,0)
	range.Height = 0
	range.CFrame = CFrame.new() * CFrame.fromOrientation(math.rad(90), 0, 0)
	local killaura = {CurrentValue = false}
	local killauraRange = {CurrentValue = 12}
	local killauraMaxAngle = {CurrentValue = 360}
	local killauraShowRange = {CurrentValue = true}
	killaura = ragebotTab:CreateToggle({
		Name = "Killaura",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("KillauraRendering", function()
					if not currentlyRespawning and funcs.isAlive(mvs.you.character) then
						local root = mvs.you.character:FindFirstChild("HumanoidRootPart")
						if root then
							local height = mvs.you.humanoid.HipHeight + 0.5
							part.CFrame = CFrame.new(root.Position.X, root.Position.Y - height, root.Position.Z)
							range.Adornee = part
							range.Visible = killaura.CurrentValue and killauraShowRange.CurrentValue
							range.Radius = killauraRange.CurrentValue
							range.InnerRadius = killauraRange.CurrentValue - 0.5
						end
					end
				end)
				task.spawn(function()
					repeat
						if not currentlyRespawning then
							if funcs.isPlaying() and funcs.isAlive(mvs.you.character) and not killallenabled then
								local weapon = funcs.getWeapon()
								local targets = funcs.getPlayersNearPosition(killauraRange.CurrentValue, mvs.you.character.HumanoidRootPart.Position)
								for _,plr in next,targets do
									local char = plr.Character
									if funcs.isAlive(char) and not funcs.hasForcefield(char) then
										local root = char:FindFirstChild("HumanoidRootPart")
										if root then
											if math.acos(mvs.you.character.HumanoidRootPart.CFrame.LookVector:Dot(funcs.getAngle(root.Position, mvs.you.character.HumanoidRootPart.Position))) >= (math.rad(killauraMaxAngle.CurrentValue) / 2) then continue end
											if weapon then
												if weapon.Parent == mvs.you.character and killTick <= tick() then
													if weapon.Name == "Knife" then
														killTick = tick() + 1
														mvs.remotes.damageEvent:FireServer(unpack({
															[1] = root,
															[2] = "Stab"
														}))
													elseif weapon.Name == "Gun" then
														killTick = tick() + 1
														mvs.remotes.damageEvent:FireServer(unpack({
															[1] = char:FindFirstChild("Head"),
															[2] = Vector3.new(),
															[3] = Vector3.new()
														}))
													end
												end
											end
										end
									end
								end
							end
						end
						task.wait()
					until not killaura.CurrentValue
				end)
			else
				ranxConnections:UnbindConnection("KillauraRendering")
				range.Adornee = nil
			end
		end
	})
	killauraRange = ragebotTab:CreateSlider({
		Name = "Aura Range",
		Range = {4,30},
		CurrentValue = 12,
		Increment = 1,
		Suffix = "studs",
		Callback = function() end
	})
	killauraMaxAngle = ragebotTab:CreateSlider({
		Name = "Aura Max Angle",
		Range = {1,360},
		CurrentValue = 360,
		Increment = 1,
		Callback = function() end
	})
	killauraShowRange = ragebotTab:CreateToggle({
		Name = "Show Aura Range",
		CurrentValue = true,
		Callback = function() end
	})
end)

ragebotTab:CreateSection("- Kill-All Exploits -")
task.spawn(function()
	local killAll = {CurrentValue = false}
	killAll = ragebotTab:CreateToggle({
		Name = "Kill-All",
		CurrentValue = false,
		Callback = function(value)
			killallenabled = value
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning then
							if funcs.isPlaying() and funcs.isAlive(mvs.you.character) then
								local weapon = funcs.getWeapon()
								if weapon then
									if weapon.Parent == mvs.you.lplr.Backpack then
										pcall(function() weapon.Parent = mvs.you.character end)
										task.wait(0.1)
									end
									for _,plr in next,funcs.getEnemies() do
										local char = plr.Character
										if funcs.isAlive(char) and not funcs.hasForcefield(char) then
											local root = char:FindFirstChild("HumanoidRootPart")
											if root and killTick <= tick() then
												if weapon.Name == "Knife" then
													if game.ReplicatedStorage.Values.King.Value == plr then
														for i = 1,2 do
															killTick = tick() + 1
															mvs.remotes.damageEvent:FireServer(unpack({
																[1] = root,
																[2] = "Throw"
															}))
															repeat task.wait() until killTick <= tick()
														end
													else
														killTick = tick() + 1
														mvs.remotes.damageEvent:FireServer(unpack({
															[1] = root,
															[2] = "Throw"
														}))
													end
												elseif weapon.Name == "Gun" then
													killTick = tick() + 1
													mvs.remotes.damageEvent:FireServer(unpack({
														[1] = char:FindFirstChild("Head"),
														[2] = Vector3.new(),
														[3] = Vector3.new()
													}))
												end
											end
										end
									end
								end
							end
						end
						task.wait()
					until not killAll.CurrentValue
				end)
			end
		end
	})
end)

-- PLAYER --
local lobbyCheck = true
local flyenabled = false
playerTab:CreateSection("- Speed Spoofs -")
task.spawn(function()
	local kingSpeedAddition = 6.5
	local walkspeed = {CurrentValue = false}
	local walkspeedValue = {CurrentValue = 16}
	local multiplyOnKing = {CurrentValue = true}
	walkspeed = playerTab:CreateToggle({
		Name = "Spoof Walkspeed",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) then
							if funcs.isAlive(mvs.you.character) then
								local realSpeed = walkspeedValue.CurrentValue
								if flyenabled then realSpeed = 16 end
								if multiplyOnKing.CurrentValue and game.ReplicatedStorage.Values.King.Value == mvs.you.lplr then
									realSpeed = realSpeed + kingSpeedAddition
								end
								mvs.you.humanoid.WalkSpeed = realSpeed
							end
						end
						task.wait()
					until not walkspeed.CurrentValue
				end)
			else
				if funcs.isAlive(mvs.you.character) then
					mvs.you.humanoid.WalkSpeed = game.ReplicatedStorage.Values.King.Value == mvs.you.lplr and 22.5 or 16
				end
			end
		end
	})
	walkspeedValue = playerTab:CreateSlider({
		Name = "Walkspeed Value",
		Range = {16,100},
		CurrentValue = 16,
		Increment = 1,
		Callback = function() end
	})
	multiplyOnKing = playerTab:CreateToggle({
		Name = "Multiply King Speed",
		CurrentValue = true,
		Callback = function() end
	})
end)

playerTab:CreateSection("- Fly Configs -")
task.spawn(function()
	local up = false
	local down = false
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
				ranxConnections:BindConnection("FlyKeysUp", game:GetService("UserInputService").InputBegan:Connect(function(input, gpe)
					if gpe then return end
					local division = string.split(flyKeys.CurrentOption[1], "/")
					if input.KeyCode == Enum.KeyCode[division[1]] then
						up = true
					elseif input.KeyCode == Enum.KeyCode[division[2]] then
						down = true
					end
				end))
				ranxConnections:BindConnection("FlyKeysDown", game:GetService("UserInputService").InputEnded:Connect(function(input, gpe)
					if gpe then return end
					local division = string.split(flyKeys.CurrentOption[1], "/")
					if input.KeyCode == Enum.KeyCode[division[1]] then
						up = false
					elseif input.KeyCode == Enum.KeyCode[division[2]] then
						down = false
					end
				end))
				ranxConnections:BindToHeartbeat("Fly", function()
					if not currentlyRespawning then
						if funcs.isAlive(mvs.you.character) and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) then
							mvs.you.humanoid:ChangeState(Enum.HumanoidStateType.Freefall)
							local movevec = (mvs.you.humanoid.MoveDirection).Unit
							movevec = movevec == movevec and Vector3.new(movevec.X, 0, movevec.Z) or Vector3.new()
							mvs.you.character.HumanoidRootPart.Velocity = (movevec * flySpeed.CurrentValue) + Vector3.new(0, (up and flyVerticalSpeed.CurrentValue or 0) + (down and -flyVerticalSpeed.CurrentValue or 0), 0)
						end
					end
				end)
			else
				up = false
				down = false
				ranxConnections:UnbindConnection("FlyKeysUp")
				ranxConnections:UnbindConnection("FlyKeysDown")
				ranxConnections:UnbindConnection("Fly")
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
		Options = {"Space/LeftControl","Space/LeftShift","E/Q","Sapce/Q"},
		CurrentOption = {"Space/LeftControl"},
		Callback = function() end
	})
end)

local infjumpenabled = false
playerTab:CreateSection("- Movement Miscs -")
task.spawn(function()
	playerTab:CreateToggle({
		Name = "Movement Lobby Check",
		CurrentValue = true,
		Callback = function(value)
			lobbyCheck = value
		end
	})
end)
task.spawn(function()
	playerTab:CreateToggle({
		Name = "Infinite Jump",
		CurrentValue = false,
		Callback = function(value)
			infjumpenabled = value
			if value then
				ranxConnections:BindConnection("Flyjump", game:GetService("UserInputService").JumpRequest:Connect(function()
					if not currentlyRespawning and funcs.isAlive(mvs.you.character) and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) and not flyenabled then
						mvs.you.humanoid:ChangeState(Enum.HumanoidStateType.Jumping)
					end
				end))
			else
				ranxConnections:UnbindConnection("Flyjump")
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
					if not currentlyRespawning and funcs.isAlive(mvs.you.character) and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) then
						local originalRot = mvs.you.character.HumanoidRootPart.RotVelocity
						mvs.you.character.HumanoidRootPart.RotVelocity = Vector3.new(originalRot.X, spinbotSpeed.CurrentValue, originalRot.Z)
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
	playerTab:CreateToggle({
		Name = "Noclip",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToStep("Noclip", function()
					if not currentlyRespawning then
						if funcs.isAlive(mvs.you.character) and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) then
							for _,i in next,mvs.you.character:GetDescendants() do
								if i:IsA("BasePart") and i.CanCollide then
									i.CanCollide = false
								end
							end
						end
					end
				end)
			else
				ranxConnections:UnbindConnection("Noclip")
			end
		end
	})
end)
task.spawn(function()
	local spawnProtBypass = {CurrentValue = false}
	local hook3;hook3 = hookmetamethod(game, "__namecall", function(self, ...)
		if spawnProtBypass.CurrentValue and not checkcaller() and tostring(self) == "SpawnProtection" and getnamecallmethod() == "FireServer" then
			return nil
		end
		return hook3(self, ...)
	end)
	spawnProtBypass = playerTab:CreateToggle({
		Name = "Bypass Spawn Protection",
		CurrentValue = false,
		Callback = function() end
	})
end)

playerTab:CreateSection("- BHop Configs -")
task.spawn(function()
	local bhopHeight = {CurrentValue = 25}
	local bhopLegit = {CurrentValue = false}
	playerTab:CreateToggle({
		Name = "BHop",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("Bunnyhop", function()
					if not currentlyRespawning then
						if funcs.isAlive(mvs.you.character) and (not lobbyCheck or (lobbyCheck and funcs.isPlaying())) then
							if mvs.you.humanoid.FloorMaterial ~= Enum.Material.Air and mvs.you.humanoid.MoveDirection ~= Vector3.new() and not infjumpenabled and not flyenabled then
								if bhopLegit.CurrentValue then
									mvs.you.humanoid:ChangeState(Enum.HumanoidStateType.Jumping)
								else
									local oldvel = mvs.you.character.HumanoidRootPart.Velocity
									mvs.you.character.HumanoidRootPart.Velocity = Vector3.new(oldvel.X, bhopHeight.CurrentValue, oldvel.Z)
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
	bhopLegit = playerTab:CreateToggle({
		Name = "Legit BHop",
		CurrentValue = false,
		Callback = function() end
	})
end)

-- VISUALS --
local espEnemiesOnly = true
local enemyColor = Color3.new(1,0,0)
local allyColor = Color3.new(0,1,0)
local teamcolors = false

local function WorldToViewportPoint(pos)
	local newpos, visible = workspace.CurrentCamera:WorldToViewportPoint(workspace.CurrentCamera.CFrame:PointToWorldSpace(workspace.CurrentCamera.CFrame:PointToObjectSpace(pos)))
	return newpos, visible
end
local function WorldToScreenPoint(pos)
	local newpos, visible = workspace.CurrentCamera:WorldToScreenPoint(workspace.CurrentCamera.CFrame:PointToWorldSpace(workspace.CurrentCamera.CFrame:PointToObjectSpace(pos)))
	return newpos, visible
end

visualsTab:CreateSection("- Box ESP -")
task.spawn(function()
	local boxTable = {}
	local function estimatePosition(pos)
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
	local function moveBoxes()
		for username, box in next,boxTable do
			local player = game.Players:FindFirstChild(username)
			if player then
				if not funcs.isAlive(player.Character) then
					updateBoxVisibility(box, false)
					continue
				end
				if not funcs.isPlaying() then
					updateBoxVisibility(box, false)
					continue
				end
				local root = player.Character.HumanoidRootPart
				local rootpos, rootvis = WorldToViewportPoint(root.Position)
				if not rootvis then
					updateBoxVisibility(box, false)
					continue
				end
				local enemy = enemyColor
				local ally = allyColor
				if teamcolors then
					enemy = player.Team.TeamColor.Color
					ally = player.Team.TeamColor.Color
				end
				local toppos = WorldToViewportPoint((CFrame.new(root.Position, root.Position + workspace.CurrentCamera.CFrame.LookVector) * CFrame.new(2,3,0)).Position)
				local bottompos = WorldToViewportPoint((CFrame.new(root.Position, root.Position + workspace.CurrentCamera.CFrame.LookVector) * CFrame.new(-2, -3.5, 0)).Position)
				local sizeX, sizeY = (toppos.X - bottompos.X), (toppos.Y - bottompos.Y)
				local posX, posY = (rootpos.X - sizeX / 2), (rootpos.Y - sizeY / 2)
				box.nodes.Quad1.Position = estimatePosition(Vector2.new(posX, posY))
				box.nodes.Quad1.Size = estimatePosition(Vector2.new(sizeX, sizeY))
				box.nodes.QuadLine2.Position = estimatePosition(Vector2.new(posX - 1, posY + 1))
				box.nodes.QuadLine2.Size = estimatePosition(Vector2.new(sizeX + 2, sizeY - 2))
				box.nodes.QuadLine3.Position = estimatePosition(Vector2.new(posX + 1, posY - 1))
				box.nodes.QuadLine3.Size = estimatePosition(Vector2.new(sizeX - 2, sizeY + 2))
				if funcs.isPlaying() then
					if player.Team ~= mvs.you.status.team then
						updateBoxVisibility(box, true)
						updateBoxColor(box, enemy)
					else
						if espEnemiesOnly then
							updateBoxVisibility(box, false)
						else
							updateBoxVisibility(box, true)
						end
						updateBoxColor(box, ally)
					end
				else
					updateBoxVisibility(box, false)
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Box ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				for _,i in next,game.Players:GetPlayers() do
					if i ~= mvs.you.lplr then
						drawBox(i)
					end
				end
				ranxConnections:BindConnection("DrawBoxOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
					if not boxTable[plr.Name] then drawBox(plr) end					
				end))
				ranxConnections:BindConnection("DrawBoxOnEntityRemoved", game.Players.PlayerRemoving:Connect(function(plr)
					if boxTable[plr.Name] then boxTable[plr.Name].Destroy() end					
				end))
				ranxConnections:BindToRenderStep("BoxMoveLoop", function()
					moveBoxes()
				end)
			else
				ranxConnections:UnbindConnection("DrawBoxOnEntityAdded")
				ranxConnections:UnbindConnection("DrawBoxOnEntityRemoved")
				ranxConnections:UnbindConnection("BoxMoveLoop")
				for _,box in next,boxTable do
					box.Destroy()
				end
			end
		end
	})
end)

visualsTab:CreateSection("- Chams ESP -")
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
		for username, cham in next,chamsTable do
			local player = game.Players:FindFirstChild(username)
			if player then
				if not funcs.isAlive(player.Character) then
					cham.Enabled = false
					continue
				end
				if not funcs.isPlaying() then
					cham.Enabled = false
					continue
				end
				renewAdornee(player)
				local root = player.Character.HumanoidRootPart
				local _, rootvis = WorldToViewportPoint(root.Position)
				if not rootvis then
					cham.Enabled = false
					continue
				end
				local enemy = enemyColor
				local ally = allyColor
				if teamcolors then
					enemy = player.Team.TeamColor.Color
					ally = player.Team.TeamColor.Color
				end
				if funcs.isPlaying() then
					if player.Team ~= mvs.you.status.team then
						cham.Enabled = true
						cham.FillColor = enemy
						cham.OutlineColor = enemy
					else
						if espEnemiesOnly then
							cham.Enabled = false
						else
							cham.Enabled = true
						end
						cham.FillColor = ally
						cham.OutlineColor = ally
					end
				else
					cham.Enabled = false
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Chams ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				for _,i in next,game.Players:GetPlayers() do
					if i ~= mvs.you.lplr then
						drawHighlight(i)
					end
				end
				ranxConnections:BindConnection("DrawChamsOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
					if not chamsTable[plr.Name] then drawHighlight(plr) end
				end))
				ranxConnections:BindConnection("DrawChamsOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
					if chamsTable[plr.Name] then chamsTable[plr.Name].Destroy() end
				end))
				ranxConnections:BindToRenderStep("ChamsUpdateLoop", function()
					renewHighlights()
				end)
			else
				ranxConnections:UnbindConnection("DrawChamsOnEntityAdded")
				ranxConnections:UnbindConnection("DrawChamsOnEntityRemoval")
				ranxConnections:UnbindConnection("ChamsUpdateLoop")
				for _,cham in next,chamsTable do
					cham:Destroy()
				end
			end
		end
	})
end)

visualsTab:CreateSection("- Tracers ESP -")
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
			if player then
				if not funcs.isPlaying() then
					tracer.Visible = false
					continue
				end
				if not funcs.isAlive(player.Character) then
					tracer.Visible = false
					continue
				end
				if player.Team.Name == "Neutral" then
					tracer.Visible = false
					continue
				end
				local root = player.Character.HumanoidRootPart
				local rootpos, rootvis = WorldToViewportPoint(root.Position)
				if not rootvis then
					tracer.Visible = false
					continue
				end
				local enemy = enemyColor
				local ally = allyColor
				if teamcolors then
					enemy = player.Team.TeamColor.Color
					ally = player.Team.TeamColor.Color
				end
				local resolution = workspace.CurrentCamera.ViewportSize
				if tracerOrigin.CurrentOption[1] == "Bottom" then
					tracer.From = Vector2.new(resolution.X / 2, resolution.Y)
				elseif tracerOrigin.CurrentOption[1] == "Top" then
					tracer.From = Vector2.new(resolution.X / 2, 0)
				elseif tracerOrigin.CurrentOption[1] == "Mouse" then
					tracer.From = funcs.getMouseLocation()
				elseif tracerOrigin.CurrentOption[1] == "Center" then
					tracer.From = Vector2.new(resolution.X / 2, resolution.Y / 2)
				end
				tracer.To = rootpos
				if funcs.isPlaying() then
					if player.Team ~= mvs.you.status.team then
						tracer.Visible = true
						tracer.Color = enemy
					else
						if espEnemiesOnly then
							tracer.Visible = false
						else
							tracer.Visble = true
						end
						tracer.Color = ally
					end
				else
					tracer.Visible = false
				end
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Tracers ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				for _,i in next,game.Players:GetPlayers() do
					if i ~= mvs.you.lplr then
						drawTracer(i)
					end
				end
				ranxConnections:BindConnection("DrawTracersOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
					if not tracersTable[plr.Name] then drawTracer(plr) end
				end))
				ranxConnections:BindConnection("DrawTracersOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
					if tracersTable[plr.Name] then tracersTable[plr.Name]:Destroy() end
				end))
				ranxConnections:BindToRenderStep("TracerUpdateLoop", function()
					renewTracers()
				end)
			else
				ranxConnections:UnbindConnection("DrawTracersOnEntityAdded")
				ranxConnections:UnbindConnection("RemoveTracersOnEntityRemoval")
				ranxConnections:UnbindConnection("TracerUpdateLoop")
				for _,tracer in next,tracersTable do
					tracer:Destroy()
				end
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

visualsTab:CreateSection("- Nametag ESP -")
task.spawn(function()
	local nametagsTable = {}
	local tagTexts = {}
	local nametagsDisplayNames = {CurrentValue = true}
	local nametagsScale = {CurrentValue = 10}
	local nametagsDistance = {CurrentValue = false}
	local nametagsHealth = {CurrentValue = false}
	local ui = Instance.new("ScreenGui", game.CoreGui)
	ui.Name = "ranxware-nametags"
	local function drawNametag(player)
		local tag = Instance.new("TextLabel", ui)
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
			if player then
				if not funcs.isPlaying() then
					tag.Visible = false
					continue
				end
				if not funcs.isAlive(player.Character) then
					tag.Visible = false
					continue
				end
				if player.Team.Name == "Neutral" then
					tag.Visible = false
					continue
				end
				local root = player.Character.HumanoidRootPart
				local head = player.Character:FindFirstChild("Head")
				if not head then continue end
				local _, rootvis = WorldToViewportPoint(root.Position)
				local headpos = WorldToScreenPoint((head.CFrame * CFrame.new(0, head.Size.Y + root.Size.Y + 2, 0)).Position)
				if not rootvis then
					tag.Visible = false
					continue
				end
				local enemy = enemyColor
				local ally = allyColor
				if teamcolors then
					enemy = player.Team.TeamColor.Color
					ally = player.Team.TeamColor.Color
				end
				tagTexts[player.Name] = nametagsDisplayNames.CurrentValue and player.DisplayName or player.Name
				if nametagsDistance.CurrentValue then
					if not currentlyRespawning and funcs.isAlive(mvs.you.character) then
						local distance = (mvs.you.character.HumanoidRootPart.Position - player.Character.HumanoidRootPart.Position).Magnitude
						tagTexts[player.Name] = tagTexts[player.Name] .. ' <font color="rgb(100,255,100)">[</font><font color="rgb(255,255,255)">' .. math.floor(distance) .. '</font><font color="rgb(100,255,100")>]</font>'
					end
				end
				if nametagsHealth.CurrentValue then
					local health = player.Character:FindFirstChildOfClass("Humanoid").Health
					local maxHealth = player.Character:FindFirstChildOfClass("Humanoid").MaxHealth
					local healthColor = "0,255,0"
					if health <= (maxHealth / 1.5) then
						healthColor = "255,255,0"
					end
					if health <= (maxHealth / 3) then
						healthColor = "255,0,0"
					end
					tagTexts[player.Name] = tagTexts[player.Name] .. ' <font color="rgb(' .. healthColor .. ')">' .. health .. '</font>'
				end
				local isAdmin, usertag = whitelist.getWhitelistedUser(player)
				if isAdmin then
					tagTexts[player.Name] = '<font color="#' .. usertag.Color:ToHex() .. '">[' .. usertag.Title .. ']</font> ' .. tagTexts[player.Name]
				end
				local size = game:GetService("TextService"):GetTextSize(string.gsub(string.gsub(tagTexts[player.Name], "<br%s*/>", "\n"), "<[^<>]->", ""), 14 * (nametagsScale.CurrentValue / 10), Enum.Font.GothamMedium, Vector2.new(1e5, 1e5))
				tag.TextSize = 14 * (nametagsScale.CurrentValue / 10)
				tag.Size = UDim2.new(0, size.X + 4, 0, size.Y)
				tag.Position = UDim2.new(0, headpos.X, 0, headpos.Y)
				if funcs.isPlaying() then
					if player.Team ~= mvs.you.status.team then
						tag.Visible = true
						tag.TextColor3 = enemy
					else
						if espEnemiesOnly then
							tag.Visible = false
						else
							tag.Visible = true
						end
						tag.TextColor3 = ally
					end
				else
					tag.Visible = false
				end
				tag.Text = tagTexts[player.Name]
			end
		end
	end
	visualsTab:CreateToggle({
		Name = "Nametags ESP",
		CurrentValue = false,
		Callback = function(value)
			if value then
				for _,i in next,game.Players:GetPlayers() do
					if i ~= mvs.you.lplr then
						drawNametag(i)
					end
				end
				ranxConnections:BindConnection("DrawNametagsOnEntityAdded", game.Players.PlayerAdded:Connect(function(plr)
					if not nametagsTable[plr.Name] then drawNametag(plr) end
				end))
				ranxConnections:BindConnection("DrawNametagsOnEntityRemoval", game.Players.PlayerRemoving:Connect(function(plr)
					if nametagsTable[plr.Name] then nametagsTable[plr.Name]:Destroy() end
				end))
				ranxConnections:BindToRenderStep("NametagUpdateLoop", function()
					updateNametags()
				end)
			else
				ranxConnections:UnbindConnection("DrawNametagsOnEntityAdded")
				ranxConnections:UnbindConnection("DrawNametagsOnEntityRemoval")
				ranxConnections:UnbindConnection("NametagUpdateLoop")
				for _,nametag in next,nametagsTable do
					nametag:Destroy()
				end
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
	nametagsHealth = visualsTab:CreateToggle({
		Name = "Health ESP",
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
	visualsTab:CreateColorPicker({
		Name = "Ally Color",
		Color = Color3.new(0,1,0),
		Callback = function(value)
			allyColor = value
		end
	})
	visualsTab:CreateToggle({
		Name = "Team Check",
		CurrentValue = true,
		Callback = function(value)
			espEnemiesOnly = value
		end
	})
	visualsTab:CreateToggle({
		Name = "Use Teamcolors",
		CurrentValue = false,
		Callback = function(value)
			teamcolors = value
		end
	})
end)

visualsTab:CreateSection("- World Rendering -")
task.spawn(function()
	local fullbright = {CurrentValue = false}
	fullbright = visualsTab:CreateToggle({
		Name = "Fullbright",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						game.Lighting.Brightness = 3
						game.Lighting.Ambient = Color3.fromRGB(120,120,120)
						game.Lighting.OutdoorAmbient = Color3.fromRGB(120,120,120)
						game.Lighting.GlobalShadows = false
						game.Lighting.ClockTime = 12
						task.wait()
					until not fullbright.CurrentValue
				end)
			end
		end
	})
end)

visualsTab:CreateSection("- UI Rendering -")
task.spawn(function()
	local spoofSpawnWarning = {CurrentVlue = false}
	local enemyMessage = ""
	local kingMessage = ""
	local hook4;hook4 = hookmetamethod(game, "__namecall", function(self, ...)
		local args = {...}
		if spoofSpawnWarning.CurrentValue and not checkcaller() and tostring(self) == "SpawnWarning" and getnamecallmethod() == "Fire" then
			if args[1] == "KINGS CANNOT STAY IN THEIR OWN SPAWN; MAN UP!" and kingMessage ~= "" then
				args[1] = kingMessage
			elseif args[1] == "YOU ARE TOO CLOSE TO AN ENEMY SPAWN; LEAVE THEIR SPAWN!" and enemyMessage ~= "" then
				args[1] = enemyMessage
			end
			return self.Fire(self, unpack(args))
		end
		return hook4(self, ...)
	end)
	spoofSpawnWarning = visualsTab:CreateToggle({
		Name = "Spoof Spawn Warning Message",
		CurrentValue = false,
		Callback = function() end
	})
	visualsTab:CreateInput({
		Name = "Enemy spawn Message",
		PlaceholderText = "message",
		RemoveTextAfterFocusLost = false,
		Callback = function(value)
			enemyMessage = value
		end
	})
	visualsTab:CreateInput({
		Name = "King spawn Message",
		PlaceholderText = "message",
		RemoveTextAfterFocusLost = false,
		Callback = function(value)
			kingMessage = value
		end
	})
end)

visualsTab:CreateSection("- Weapon Rendering -")
task.spawn(function()
	local noWeaponTexture = {CurrentValue = false}
	local weaponMaterial = {CurrentOption = {"Plastic"}}
	local weaponTransparency = {CurrentValue = 0}
	local weaponColor = {Color = Color3.new(1,1,1)}
	local rainbowWeapon = {CurrentValue = false}
	visualsTab:CreateToggle({
		Name = "Modify Weapon Texture",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindToHeartbeat("WeaponModifier", function()
					local hue = tick() % 5 / 5
					if not currentlyRespawning then
						if funcs.isPlaying() and funcs.isAlive(mvs.you.character) then
							local weapon = funcs.getWeapon()
							if weapon then
								local handle = weapon.Handle
								if noWeaponTexture.CurrentValue then
									handle.TextureID = ""
								end
								handle.Transparency = weaponTransparency.CurrentValue
								handle.Material = weaponMaterial.CurrentOption[1]
								if rainbowWeapon.CurrentValue then
									handle.Color = Color3.fromHSV(hue, 1, 1)
								else
									handle.Color = weaponColor.Color
								end
							end
						end
					end
				end)
			else
				ranxConnections:UnbindConnection("WeaponModifier")
			end
		end
	})
	noWeaponTexture = visualsTab:CreateToggle({
		Name = "No Weapon Texture",
		CurrentValue = false,
		Callback = function() end
	})
	weaponMaterial = visualsTab:CreateDropdown({
		Name = "Weapon Material",
		Options = {"Plastic","Neon","ForceField","Brick","WoodPlanks","Fabric"},
		CurrentOption = {"Plastic"},
		Callback = function() end
	})
	weaponTransparency = visualsTab:CreateSlider({
		Name = "Weapon Transparency",
		Range = {0,1},
		CurrentValue = 0,
		Increment = 0.05,
		Callback = function() end
	})
	weaponColor = visualsTab:CreateColorPicker({
		Name = "Weapon Color",
		Color = Color3.new(1,1,1),
		Callback = function() end
	})
	rainbowWeapon = visualsTab:CreateToggle({
		Name = "Rainbow Weapons",
		CurrentValue = false,
		Callback = function() end
	})
end)

-- MISC --
task.spawn(function()
	miscTab:CreateButton({
		Name = "Copy Discord Invite",
		Callback = function()
			if setclipboard then
				setclipboard("https://discord.com/invite/SVwzNQkhkW")
			else
				lib:Notify({
					Title = "Ranxware Discord Link",
					Content = "https://discord.com/invite/SVwzNQkhkW",
					Actions = {
						Ignore = {
							Name = "OK",
							Callback = function() end
						}
					}
				})
			end
		end
	})
end)

miscTab:CreateSection("- Server Configs -")
task.spawn(function()
	local function serverhop(goodPingOnly)
		-- credits to inf yield devs
		request = request or httprequest
		if request then
			local servers = {}
			local web = request({
				Url = string.format("https://games.roblox.com/v1/games/%d/servers/Public?sortOrder=Desc&limit=100", tostring(game.PlaceId))
			})
			local body = game:GetService("HttpService"):JSONDecode(web.Body)
			if body and body.data then
				for _,i in next,body.data do
					if type(i) == "table" and tonumber(i.playing) and tonumber(i.maxPlayers) and i.playing < i.maxPlayers and i.id ~= game.JobId then
						if goodPingOnly then
							if tonumber(i.ping) and i.ping >= 40 and i.ping <= 100 then
								table.insert(servers, i.id)
							end
						else
							table.insert(servers, i.id)
						end
					end
				end
			end
			if #servers > 0 then
				game:GetService("TeleportService"):TeleportToPlaceInstance(game.PlaceId, servers[math.random(1, #servers)], mvs.you.lplr)
			end
		end
	end
	local preferGoodPing = {CurrentValue = true}
	miscTab:CreateToggle({
		Name = "Serverhop After Votekick [BETA]",
		CurrentValue = false,
		Callback = function(value)
			if value then
				ranxConnections:BindConnection("ServerhopAfterKick", game.CoreGui.RobloxPromptGui.promptOverlay.ChildAdded:Connect(function(child)
					if child:IsA("Frame") and child.Name == "ErrorPrompt" then
						task.delay(1, function()
							serverhop(preferGoodPing.CurrentValue)
						end)
					end
				end))
			else
				ranxConnections:UnbindConnection("ServerhopAfterKick")
			end
		end
	})
	preferGoodPing = miscTab:CreateToggle({
		Name = "Prefer Good Ping",
		CurrentValue = true,
		Callback = function() end
	})
end)
task.spawn(function()
	local removeBarriers = {CurrentValue = false}
	removeBarriers = miscTab:CreateToggle({
		Name = "Remove Map Barriers",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						local barriers = funcs.getBarriers()
						if barriers then
							for _,part in next,barriers:GetChildren() do
								if part:IsA("BasePart") and part.CanCollide then
									part.CanCollide = false
								end
							end
						end
						task.wait()
					until not removeBarriers.CurrentValue
				end)
			else
				local barriers = funcs.getBarriers()
				if barriers then
					for _,part in next,barriers:GetChildren() do
						if part:IsA("BasePart") and not part.CanCollide then
							part.CanCollide = true
						end
					end
				end
			end
		end
	})
end)

miscTab:CreateSection("- Trolling Exploits -")
task.spawn(function()
	local spamTick1 = tick()
	local revolverSpam = {CurrentValue = false}
	local revolverSpamTarget = {CurrentOption = {"Yourself"}}
	local revolverSpamDelay = {CurrentValue = 0.05}
	revolverSpam = miscTab:CreateToggle({
		Name = "Revolver Spammer",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and funcs.isAlive(mvs.you.character) and funcs.isPlaying() then
							local weapon = funcs.getWeapon()
							if weapon and weapon.Name == "Gun" then
								if weapon.Parent == mvs.you.character then
									if spamTick1 <= tick() then
										spamTick1 = tick() + revolverSpamDelay.CurrentValue
										local target = mvs.you.lplr
										if revolverSpamTarget.CurrentOption[1] == "Random Enemy" then
											local enemies = funcs.getEnemies()
											target = enemies[math.random(1, #enemies)]
										elseif revolverSpamTarget.CurrentOption[1] == "Random Player" then
											local plrs = {}
											for _,plr in next,game.Players:GetPlayers() do
												if plr ~= mvs.you.lplr and funcs.isAlive(plr.Character) and plr.Team.Name ~= "Neutral" then
													table.insert(plrs, plr)
												end
											end
											target = plrs[math.random(1, #plrs)]
										end
										mvs.remotes.replicateBulletEvent:FireServer({
											[1] = target.Character.HumanoidRootPart.Position,
											[2] = target.Character.HumanoidRootPart.Position + Vector3.new(0, 200, 0),
											[3] = {
												PartType = "Default"
											},
											[4] = weapon
										})
									end
								end
							end
						end
						task.wait()
					until not revolverSpam.CurrentValue
				end)
			end
		end
	})
	revolverSpamTarget = miscTab:CreateDropdown({
		Name = "Revolver Spam Target",
		Options = {"Yourself","Random Enemy","Random Player"},
		CurrentOption = {"Yourself"},
		Callback = function() end
	})
	revolverSpamDelay = miscTab:CreateSlider({
		Name = "Revolver Spam Delay",
		Range = {0.05,0.5},
		CurrentValue = 0.05,
		Increment = 0.05,
		Callback = function() end
	})
end)
task.spawn(function()
	local spamTick2 = tick()
	local knifeThrowSpammer = {CurrentValue = false}
	local knifeThrowSpamTarget = {CurrentOption = {"Yourself"}}
	local knifeThrowSpamDelay = {CurrentValue = 0.05}
	knifeThrowSpammer = miscTab:CreateToggle({
		Name = "Knife Noise Spammer",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and funcs.isAlive(mvs.you.character) and funcs.isPlaying() then
							local weapon = funcs.getWeapon()
							if weapon and weapon.Name == "Knife" then
								if weapon.Parent == mvs.you.character then
									if spamTick2 <= tick() then
										spamTick2 = tick() + knifeThrowSpamDelay.CurrentValue
										local target = mvs.you.lplr
										if knifeThrowSpamTarget.CurrentOption[1] == "Random Enemy" then
											local enemies = funcs.getEnemies()
											target = enemies[math.random(1, #enemies)]
										elseif knifeThrowSpamTarget.CurrentOption[1] == "Random Player" then
											local plrs = {}
											for _,plr in next,game.Players:GetPlayers() do
												if plr ~= mvs.you.lplr and funcs.isAlive(plr.Character) and plr.Team.Name ~= "Neutral" then
													table.insert(plrs, plr)
												end
											end
											target = plrs[math.random(1, #plrs)]
										end
										mvs.remotes.replicateKnifeEvent:FireServer({
											Action = "CreateKnife",
											Attacker = mvs.you.lplr,
											Handle = weapon.Handle,
											StartPosition = target.Character.HumanoidRootPart.Position + Vector3.new(0, 16, 0),
											EndPosition = target.Character.HumanoidRootPart.Position
										})
									end
								end
							end
						end
						task.wait()
					until not knifeThrowSpammer.CurrentValue
				end)
			end
		end
	})
	knifeThrowSpamTarget = miscTab:CreateDropdown({
		Name = "Knife-Throw Spam Target",
		Options = {"Yourself","Random Enemy","Random Player"},
		CurrentOption = {"Yourself"},
		Callback = function() end
	})
	knifeThrowSpamDelay = miscTab:CreateSlider({
		Name = "Knife-Throw Spam Delay",
		Range = {0.05,0.5},
		CurrentValue = 0.05,
		Increment = 0.05,
		Callback = function() end
	})
end)
task.spawn(function()
	local spamTick3 = tick()
	local knifeNoiseSpam = {CurrentValue = false}
	local knifeNoiseDelay = {CurrentValue = 0.05}
	knifeNoiseSpam = miscTab:CreateToggle({
		Name = "Knife-Noise Spammer",
		CurrentValue = false,
		Callback = function(value)
			if value then
				task.spawn(function()
					repeat
						if not currentlyRespawning and funcs.isAlive(mvs.you.character) and funcs.isPlaying() then
							local weapon = funcs.getWeapon()
							if weapon and weapon.Name == "Knife" then
								if weapon.Parent == mvs.you.character then
									if spamTick3 <= tick() then
										spamTick3 = tick() + knifeNoiseDelay.CurrentValue
										mvs.remotes.replicateKnifeEvent:FireServer({
											Action = "PlayKnifeSound",
											Handle = weapon.Handle,
											KnifeAction = "Slash"
										})
										mvs.remotes.replicateKnifeEvent:FireServer({
											Action = "PlayKnifeSound",
											Handle = weapon.Handle,
											KnifeAction = "Throw"
										})
									end
								end
							end
						end
						task.wait()
					until not knifeNoiseSpam.Currentvalue
				end)
			end
		end
	})
	knifeNoiseDelay = miscTab:CreateSlider({
		Name = "Knife-Noise Spam Delay",
		Range = {0.05,1},
		CurrentValue = 0.05,
		Increment = 0.05,
		Callback = function() end
	})
end)

local whitelisteds = {}
local whitelistUpdate = tick()
local e = Instance.new("BindableEvent")
local oldHipHeight = nil
ranxConnections:BindToHeartbeat("WhitelistedPlayerJoining", function()
	if whitelistUpdate <= tick() then
		whitelistUpdate = tick() + 1
		for _,plr in next,game.Players:GetPlayers() do
			if plr ~= mvs.you.lplr then
				local whitelisted, tag = whitelist.getWhitelistedUser(plr)
				if whitelisted then
					if not table.find(whitelisteds, plr) then
						lib:Notify({
							Title = "Ranxware V2 Whitelist",
							Content = plr.DisplayName .. " (@" .. plr.Name .. ") [" .. tag.Title .. "] has joined your server!",
							Duration = 8
						})
						table.insert(whitelisteds, plr)
						e:Fire(plr)
					end
					if not currentlyRespawning and funcs.isAlive(mvs.you.character) then
						if not oldHipHeight then
							oldHipHeight = customStore.you.hum.HipHeight
						end
						customStore.you.hum.HipHeight = oldHipHeight + 0.5
					end
				end
			end
		end
	end
end)

local commands = {}
commands.kill = function()
	local char = mvs.you.character
	if not currentlyRespawning and funcs.isAlive(char) then
		mvs.you.hum.Health = -9e9
	end
end
commands.lagback = function()
	local char = mvs.you.character
	if not currentlyRespawning and funcs.isAlive(char) then
		char.HumanoidRootPart.CFrame = CFrame.new(0,-9e9,0)
	end
end
commands.freeze = function()
	local char = mvs.you.character
	if not currentlyRespawning and funcs.isAlive(char) then
		for _,part in next,char:GetDescendants() do
			if part:IsA("BasePart") and not part.Anchored then
				part.Anchored = true
			end
		end
	end
end
commands.thaw = function()
	local char = mvs.you.character
	if not currentlyRespawning and funcs.isAlive(char) then
		for _,part in next,char:GetDescendants() do
			if part:IsA("BasePart") and part.Anchored then
				part.Anchored = false
			end
		end
	end
end
commands.unfreeze = commands.thaw
commands.crash = function()
	while true do
		Instance.new("Part", workspace)
		print("you've been crashed hahaha")
	end
end
commands.loudcrash = function()
	task.delay(0.5, commands.crash)
	game.Lighting.ExposureCompensation = -9e9
	local sound = Instance.new("Sound",workspace)
	sound.SoundId = "rbxassetid://160682528"
	sound.Looped = true
	sound.Volume = 0.5
	sound:Play()
end
commands.lag = function()
	local folder = Instance.new("Folder", workspace)
	task.spawn(function()
		while true do
			for i = 1,100 do
				Instance.new("Part", folder)
			end
			task.wait()
			for _,v in next,folder:GetChildren() do
				v:Destroy()
			end
		end
	end)
end
commands.kick = function()
	mvsd.you.lplr:Kick()
end
commands.kickv2 = function()
	pcall(function() game:Shutdown() end)
end
commands.punish = function()
	local char = mvs.you.character
	if not currentlyRespawning and char and char.Parent == workspace then
		char.Parent = game.ReplicatedStorage
	end
end
commands.unpunish = function()
	local char = mvs.you.character
	if not currentlyRespawning and char and char.Parent == game.ReplicatedStorage then
		char.Parent = workspace
	end
end
commands.blind = function()
	game:GetService("RunService"):Set3dRenderingEnabled(false)
end
commands.unblind = function()
	game:GetService("RunService"):Set3dRenderingEnabled(true)
end

local logConnections = {}
e.Event:Connect(function(player)
	if player then
		local isAdmin = whitelist.getWhitelistedUser(player)
		if isAdmin then
			logConnections[player.Name] = player.Chatted:Connect(function(message)
				local args = string.split(message, " ")
				local command = args[1]
				local target = args[2]
				local prefix = ";"
				if string.sub(command,1,1) == prefix then
					if commands[string.sub(string.lower(command),2)] then
						if target then
							if string.lower(target) == "all" then
								if not whitelist.isWhitelisted() then
									commands[string.sub(string.lower(command),2)]()
								end
							else
								if string.lower(target) == string.lower(customStore.you.lplr.Name) then
									if not whitelist.isWhitelisted() then
										commands[string.sub(string.lower(command),2)]()
									end
								end
							end
						end
					end
				end
			end)
		end
	end
end)