import {Icon, Navigation, Tooltip} from "@shopify/polaris"
import {HomeMinor,OrdersMinor, CustomersMinor,AnalyticsMinor,DiscountsMinor,ProductsMinor,SettingsMinor} from "@shopify/polaris-icons"
import {useState} from "react"
import {useNavigate} from "react-router-dom"

import './LeftNav.css'
import Store from "../../../store"

export default function LeftNav(){

  const navigate = useNavigate();
  
  const leftNavSelected = Store((state) => state.leftNavSelected)
  const setLeftNavSelected = Store((state) => state.setLeftNavSelected)
  const leftNavCollapsed = Store((state) => state.leftNavCollapsed)
  const toggleLeftNavCollapsed = Store(state => state.toggleLeftNavCollapsed)

  const handleSelect = (selectedId) => {
    setLeftNavSelected(selectedId);
  };

    const navigationMarkup = (
      <div className={leftNavCollapsed ? 'collapse' : ''}>
        <Navigation location="/"> 
          <Navigation.Section
            items={[
                {
                  label: leftNavCollapsed? (
                    <Tooltip content="Quick Start" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={HomeMinor} />
                    </Tooltip>
                  ) : 'Quick Start',
                  icon: leftNavCollapsed ? '' : HomeMinor,
                  onClick: ()=>{
                    if (leftNavCollapsed) {
                      toggleLeftNavCollapsed()
                    } 
                    handleSelect("quick_start")
                    navigate("/dashboard/quick-start")
                  },
                  selected: leftNavSelected === 'quick_start',
                  key: '1',
                },
                {
                  label: leftNavCollapsed ? (
                    <Tooltip content="Dashboard" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={OrdersMinor} />
                    </Tooltip>
                  ) : 'Dashboard',
                  icon: leftNavCollapsed ? '' : OrdersMinor,
                  onClick: ()=>{
                    if (leftNavCollapsed) {
                      toggleLeftNavCollapsed()
                    } 
                    handleSelect("dashboard")
                    navigate("/dashboard")
                  },
                  selected: leftNavSelected === 'dashboard',
                  key: '2',
                },
                {   
                  url: '#',
                  label: leftNavCollapsed? (
                    <Tooltip content="API Inventory" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={ProductsMinor} />
                    </Tooltip>
                  ) : 'API Inventory',
                  icon: leftNavCollapsed ? '' : ProductsMinor,
                  onClick: ()=>{
                      if (leftNavCollapsed) {
                        toggleLeftNavCollapsed()
                      }
                      handleSelect("inventory")
                    },
                    selected: leftNavSelected === 'inventory',
                    subNavigationItems:[
                      {
                        label: 'Sensitive data',
                        onClick: ()=>{
                          navigate('/dashboard/observe/sensitive')
                        },
                        
                      }
                    ],
                    key: '3',
                },
                {
                  url: '#',
                  label: leftNavCollapsed? (
                    <Tooltip content="Testing" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={CustomersMinor} />
                    </Tooltip>
                  ) : 'Testing',
                  icon: leftNavCollapsed ? '' : CustomersMinor,
                  onClick: ()=>{
                      if(leftNavCollapsed){
                        toggleLeftNavCollapsed()
                      } 
                      handleSelect('testing')
                  },
                  selected: leftNavSelected === 'testing',
                  subNavigationItems:[
                    {
                      label: 'Create Tests',
                      onClick: ()=>{
                        navigate('/dashboard/testing')
                      }
                    },
                    {
                      label: 'Results',
                      onClick: ()=>{
                        navigate('/dashboard/testing')
                      }
                    }
                  ],
                  key: '4',
                },
                {
                  label: leftNavCollapsed? (
                    <Tooltip content="Test Editor" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={DiscountsMinor} />
                    </Tooltip>
                  ) : 'Test Editor',
                  icon: leftNavCollapsed ? '' : DiscountsMinor,
                  onClick: ()=>{
                    if (leftNavCollapsed) {
                      toggleLeftNavCollapsed()
                    } 
                    handleSelect("test-editor")
                    navigate("/dashboard/test-editor/REMOVE_TOKENS")
                  },
                  selected: leftNavSelected === 'test-editor',
                  key: '5',
                },
                {
                  label: leftNavCollapsed ? (
                      <Tooltip content="Issues" preferredPosition="bottom" dismissOnMouseOut>
                        <Icon source={AnalyticsMinor} />
                      </Tooltip>
                    ) : 'Issues',
                  icon: leftNavCollapsed ? '' : AnalyticsMinor,
                  onClick: ()=>{
                      if (leftNavCollapsed) {
                        toggleLeftNavCollapsed()
                      } 
                      handleSelect("issues")
                      navigate("/dashboard/issues")
                    },
                    selected: leftNavSelected === 'issues',
                    key: '6',
                },
              ]}
          />
          <Navigation.Section 
               items={[
                {
                  label: leftNavCollapsed   ? (
                    <Tooltip content="Settings" preferredPosition="bottom" dismissOnMouseOut>
                      <Icon source={SettingsMinor} />
                    </Tooltip>
                  ) : 'Settings',
                  icon: leftNavCollapsed ? '' : SettingsMinor,
                  onClick: ()=>{
                    navigate("/dashboard/settings/about")
                  },
                  selected: leftNavSelected === 'settings',
                  key: '7',
                }
              ]}
          />
        </Navigation>
        </div>
      );

    return(
        navigationMarkup
    )
}