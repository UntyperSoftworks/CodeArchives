-- SOME THINGS MAY NOT WORK WELL! --
-- PLEASE BE CAUTIOUS ABOUT IT! --

-- loadstring(game:HttpGet("https://raw.githubusercontent.com/Zer0ids/Qwerty/main/Fall.lua", true))()
local humanoid = game:GetService("Players").LocalPlayer.Character:FindFirstChildOfClass("Humanoid")

local lib = loadstring(game:HttpGet(('https://raw.githubusercontent.com/shlexware/Orion/main/source')))()
local window = lib:MakeWindow({Name = "Fall UI",HidePremium = false,SaveConfig = false,IntroEnabled = true,IntroText = "Fall [Roblox] Game Cheats v1"})
local tab = window:MakeTab({Name = "Game / Client",PremiumOnly = false})
local tab2 = window:MakeTab({Name = "Player",PremiumOnly = false})
local tab3 = window:MakeTab({Name = "Other / Misc",PremiumOnly = false})
-- tab 1 [Game / Client] --
tab:AddButton({Name = "Remove Vultures",Callback = function()
	for _,v in pairs(workspace:GetDescendants()) do
		if v:IsA("BasePart") then
			if v.Name == "Vulture" then
				v:Destroy()
			end
		end
	end
end})
tab:AddButton({Name = "TP to End",Callback = function()
	local root = game:GetService("Players").LocalPlayer.Character:FindFirstChild("HumanoidRootPart")
	root.CFrame = workspace:FindFirstChild("Pole").CFrame
end})
tab:AddButton({Name = "TP to Light",Callback = function()
	local root = game:GetService("Players").LocalPlayer.Character:FindFirstChild("HumanoidRootPart")
	root.CFrame = workspace:FindFirstChild("Light").CFrame
end})
local fallprevent = false
local fallpreventcallback = nil
tab:AddToggle({Name = "Prevent Falling",Default = false,Callback = function(bool)
	fallprevent = bool
	if fallprevent then
		if fallprevent == true then
			fallpreventcallback = humanoid.Seated:Connect(function(bool)
				if bool then
					humanoid:ChangeState(Enum.HumanoidStateType.Climbing)
				end
			end)
		elseif fallprevent == false then
			fallpreventcallback:Disconnect()
		end
	end
end})
local autosafety = false
local autosafetycallback = nil
tab:AddToggle({Name = "Auto-safety",Default = false,Callback = function(bool)
	autosafety = bool
	if autosafety then
		if autosafety == true then
			autosafetycallback = humanoid.Seated:Connect(function(bool)
				if bool then
					humanoid:ChangeState(Enum.HumanoidStateType.Climbing)
					game.ReplicatedStorage.Remotes.SavingRopeEvent:FireServer(game.Players.LocalPlayer.Name)
				end
			end)
		elseif autosafety == false then
			autosafetycallback:Disconnect()
		end
	end
end})
-- tab 2 [Player] --
local walkspeed = 12
local walkspeedtoggle = false
tab2:AddToggle({Name = "Toggle Walkspeed",Default = false,Callback = function(bool)
	walkspeedtoggle = bool
	if walkspeedtoggle == false then
		humanoid.WalkSpeed = 12
	end
end})
tab2:AddSlider({Name = "Walkspeed Modifier",Min = 12,Max = 250,Default = 12,Increment = 2,Callback = function(ws)
	if walkspeedtoggle then
		if walkspeedtoggle == true then
			humanoid.WalkSpeed = ws
		end
	end
end})
local jumppower = 50
local jumppowertoggle = false
tab2:AddToggle({Name = "Toggle Jump Power",Default = false,Callback = function(bool)
	jumppowertoggle = bool
	if jumppowertoggle == false then
		humanoid.JumpPower = 50
	end
end})
tab2:AddSlider({Name = "JumpPower Modifier",Min = 50,Max = 350,Default = 12,Increment = 2,Callback = function(jp)
	if jumppowertoggle then
		if jumppowertoggle == true then
			if humanoid.UseJumpPower == false then
				humanoid.UseJumpPower = true
			end
			humanoid.JumpPower = jp
		end
	end
end})
local infjump = nil
tab2:AddToggle({Name = "Infinite Jump",Default = false,Callback = function(infjumptoggle)
	if infjumptoggle then
		if infjumptoggle == true then
			infjump = game.Players.LocalPlayer:GetMouse().KeyDown:Connect(function(key)
				key = string.lower(key)
				if string.byte(key) == 32 then
					humanoid:ChangeState(Enum.HumanoidStateType.Jumping)
					task.wait()
					humanoid:ChangeState(Enum.HumanoidStateType.Seated)
				end
			end)
		elseif infjumptoggle == false then
			infjump:Disconnect()
		end
	end
end})
-- tab 3 [Other / Misc] --
local crashactive = false
tab3:AddButton({Name = "Crash Server",Callback = function()
	if crashactive == false then
		crashactive = true
	elseif crashactive == true then
		-- this might not work, but i'll wait until it somewhat doesn't work
		for _,plr in pairs(game.Players:GetPlayers()) do
			if plr then
				game:GetService("RunService").RenderStepped:Connect(function()
					game.ReplicatedStorage.Remotes.SavingRopeEvent:FireServer(plr.Name)
					task.wait()
				end)
			end
		end
	end
end})
tab3:AddButton({Name = "Infinite Yield [Recent Version]",Callback = function()
	loadstring(game:HttpGet('https://raw.githubusercontent.com/EdgeIY/infiniteyield/master/source'))()
end})
tab3:AddToggle({Name = "Remove Music",Default = false,Callback = function(bool)
	if bool == true then
		workspace.Sounds.MusicSound:Pause()
	elseif bool == false then
		workspace.Sounds.MusicSound:Play()
	end
end})
tab3:AddToggle({Name = "Remove Ambience Sounds",Default = false,Callback = function(bool)
	if bool == true then
		workspace.Sounds.AmbientSound:Pause()
	elseif bool == false then
		workspace.Sounds.AmbientSound:Play()
	end
end})
tab3:AddButton({Name = "???",Callback = function()
	game:Shutdown()
end})

lib:Init()
