local plr = game.Players.LocalPlayer

if not game.PlaceId == 5901548022 then
  plr:Kick("...")
end

local Orion = loadstring(game:HttpGet(('https://raw.githubusercontent.com/shlexware/Orion/main/source')))()
local window = Orion:MakeWindow({Name = "Try to Die UI",HidePremium = false,SaveConfig = false,IntroEnabled = true})
local main = window:MakeTab({Name = "Main",Icon = "",PremiumOnly = false})
main:AddLabel("There's not that much features in this script...")
main:AddLabel("However, I'll try updating this script on another time!")
local Player = main:AddSection({Name = "Player"})
Player:AddButton({Name = "Auto-win",Callback = function()
      repeat task.wait(1.5)
        plr.Character:FindFirstChildOfClass("Humanoid").Health = 0
      until plr.leaderstats.Level.Value == 25
end})
Player:AddButton({Name = "Third Person",Callback = function()
      plr.CameraMode = Enum.CameraMode.Custom -- idk if this will work or not...
end})
Player:AddButton({Name = "Skip Level",Callback = function()
      plr.Character:FindFirstChildOfClass("Humanoid").Health = 0
end})
Player:AddButton({Name = "Boom Yourself",Callback = function()
      local boom = Instance.new("Explosion")
      boom.Parent = workspace
      boom.Position = plr.Character:WaitForChild("HumanoidRootPart").Position
end})
local Game = main:AddSection({Name = "Game"})
Game:AddButton({Name = "Loop Fullbright", Callback = function()
      local function brightFunc()
            game.Lighting.Brightness = 2
            game.Lighting.ClockTime = 14
            game.Lighting.FogEnd = 100000
            game.Lighting.GlobalShadows = false
            game.Lighting.OutdoorAmbient = Color3.fromRGB(128, 128, 128)
      end
      game:GetService("RunService").RenderStepped:Connect(brightFunc)
end})
Game:AddButton({Name = "Cheat on Level 9",Callback = function()
      if plr.leaderstats.Level.Value == 9 then
        if firetouchinterest then
          firetouchinterest(plr.Character:WaitForChild("HumanoidRootPart"), workspace.Levels.Level9.Button, nil)
        else
          Orion:MakeNotification({Name = "Error!",Content = "We are sorry, but your executor doesn't support the function: 'firetouchinterest'!",Image = "",Time = 5})
        end
      else
        Orion:MakeNotification({Name = "Error!",Content = "You are not on level 9!",Image = "",Time = 5})
      end
end})
Game:AddButton({Name = "Cheat on Level 11",Callback = function()
      if plr.leaderstats.Level.Value == 11 then
        for _,part in pairs(workspace.Levels.Level11:GetDescendants()) do
          if part:IsA("BasePart") then
            if part.Transparency == 1 then
              part.Transparency = 0
            end
          end
        end
      else
        Orion:MakeNotification({Name = "Error!",Content = "You are not on level 11!",Image = "",Time = 5})
      end
end})
Game:AddButton({Name = "Cheat on Level 12",Callback = function()
      if plr.leaderstats.Level.Value == 12 then
        for _,part in pairs(workspace.Levels.Level12:GetDescendants()) do
          if part:IsA("BasePart") then
            if part.Transparency == .5 then
              part:Destroy()
            end
          end
        end
      else
        Orion:MakeNotification({Name = "Error!",Content = "You are not on level 12!",Image = "",Time = 5})
      end
end})
Game:AddButton({Name = "Cheat on Level 18",Callback = function()
      if plr.leaderstats.Level.Value == 18 then
        for _,zombie in pairs(workspace.LocalParts:GetDescendants()) do
          if zombie:IsA("BasePart") then
            if zombie.Name == "HumanoidRootPart" then
              zombie.CFrame = plr.Character:WaitForChild("HumanoidRootPart").CFrame
            end
          end
        end
      else
        Orion:MakeNotification({Name = "Error!",Content = "You are not on level 18!",Image = "",Time = 5})
      end
end})
local Misc = main:AddSection({Name = "Miscs"})
Misc:AddSlider({Name = "Set Walkspeed",Min = 16,Max = 100,Default = 16,Color = Color3.new(1,1,1),Increment = 1,ValueName = "",Callback = function(value)
      plr.Character:FindFirstChildOfClass("Humanoid").WalkSpeed = value
end})
Misc:AddSlider({Name = "Set JumpPower",Min = 50,Max = 200,Default = 50,Color = Color3.new(1,1,1),Increment = 2,ValueName = "",Callback = function(value)
      plr.Character:FindFirstChildOfClass("Humanoid").JumpPower = value
end})
Misc:AddButton({Name = "Infinite Yield (v5.9)",Callback = function()
      loadstring(game:HttpGet('https://raw.githubusercontent.com/EdgeIY/infiniteyield/master/source'))() -- inf yield v5.9
end})

Orion:Init()
