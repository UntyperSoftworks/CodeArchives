local self = {}

local conn

function self:Load()
	conn = game:GetService("RunService").Stepped:Connect(function()
		for _,v in pairs(game.Workspace:GetDescendants()) do
			if v:IsA("MeshPart") and v.MeshId == "rbxassetid://8483581926" then
				game.Players.LocalPlayer.Character:FindFirstChild("HumanoidRootPart").CFrame = v.CFrame
			end
		end
	end)
end
function self:Unload()
	conn:Disconnect()
end

return self
