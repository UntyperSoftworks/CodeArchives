-- mvsd script: silent aim standalone feature script

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

local silentAimParams = RaycastParams.new()
silentAimParams.RespectCanCollide = true
silentAimParams.FilterType = Enum.RaycastFilterType.Exclude

local silentAimHook;silentAimHook = hookmetamethod(game, "__namecall", function(self, ...)
	local args = {...}
	if not checkcaller() and getnamecallmethod() == "FireServer" then
		if tostring(self) == "Shoot" then
			local target = funcs.getPlayerNearestCursor(100)
			if target then
				local hitbox = target.Character.HumanoidRootPart
				args[2] = hitbox.CFrame.Position
				silentAimParams.FilterDescendantsInstances = {mvsd.you.character, funcs.getAllies()}
				local result = workspace:Raycast(args[1], funcs.getAngle(hitbox.CFrame.Position, args[1]) * 9e9, silentAimParams)
				if result then
					args[3] = result.Instance
					args[4] = result.Position
				else
					args[3] = hitbox.Part
					args[4] = hitbox.CFrame.Position
				end
			end
		elseif tostring(self) == "ThrowStart" then
			local target = funcs.getPlayerNearestCursor(silentAimFOVSize.CurrentValue)
			if target then
				local hitbox = target.Character.HumanoidRootPart
				args[2] = hitbox.CFrame.Position
				silentAimParams.FilterDescendantsInstances = {mvsd.you.character, funcs.getAllies()}
				local result = workspace:Raycast(args[1], funcs.getAngle(hitbox.CFrame.Position, args[1]) * 9e9, silentAimParams)
				local angle = funcs.getAngle(hitbox.CFrame.Position, args[1])
				if result then
					args[2] = funcs.getAngle(hitbox.CFrame.Position, result.Position)
					if result.Instance.Parent == target.Character then
						mvsd.remotes.ThrowHit:FireServer(unpack({
							[1] = hitbox.Part,
							[2] = Vector3.new()
						}))
					end
				else
					args[2] = angle
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
